/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.storage;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import cz.cvut.fit.bouchja1.ensemble.bandits.Bandit;
import cz.cvut.fit.bouchja1.ensemble.bandits.BanditsMachine;
import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.bandits.util.MathUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;

/**
 *
 * @author jan
 */
public class CassandraStorage implements IStorage {

    private final Log logger = LogFactory.getLog(getClass());
    private Cluster cluster;
    private Session session;
    private String keyspace;
    private String node;

    CassandraStorage(Environment env) {
        this.node = env.getProperty("cassandra.host");
        this.keyspace = env.getProperty("cassandra.keyspace");        
        connect();
        createSchema();
    }

    /*
     * You can execute queries by calling the execute method on your session object.
     * The session maintains multiple connections to the cluster nodes, provides policies
     * to choose which node to use for each query (round-robin on all nodes of the cluster
     * by default), and handles retries for failed queries when it makes sense.
     * 
     * Session instances are thread-safe and usually a single instance is all you need per application
     */
    private void connect() {
        //a contact point (node IP address) using the Cluster.Build auxiliary class
        //builds a cluster instance
        cluster = Cluster.builder()
                .addContactPoint(node)
                .build();
        //retrieves metadata from the cluster
        Metadata metadata = cluster.getMetadata();
        logger.info("Connected to cluster: " + metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            logger.info("Datacenter: " + host.getDatacenter() + "; Host: " + host.getAddress() + "; Rack: " + host.getRack());
        }
        session = cluster.connect();
    }

    //http://www.datastax.com/documentation/cql/3.1/cql/cql_reference/create_keyspace_r.html
    
    private void createSchema() {        
        session.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace + " WITH replication "
                + "= {'class':'SimpleStrategy', 'replication_factor':3};");

        //http://www.datastax.com/documentation/cql/3.1/cql/cql_reference/create_table_r.html
        session.execute(
                "CREATE TABLE IF NOT EXISTS " + keyspace + ".supercollection ("
                + "supercollection_id text,"
                + "collection_set set<varchar>,"
                + "PRIMARY KEY (supercollection_id)"
                + ");");

        
        session.execute(
                "CREATE TABLE IF NOT EXISTS " + keyspace + ".collection ("             
                + "collection_id text,"
                + "algorithm_set set<varchar>,"
                + "PRIMARY KEY (collection_id)"
                + ");");

        session.execute(
                "CREATE TABLE IF NOT EXISTS " + keyspace + ".algorithm ("
                + "collection_id text,"
                + "algorithm_id text,"
                + "event_time timestamp,"
                + "probability_in_time double,"
                + "trials_rate double,"
                + "successes_rate double,"
                + "PRIMARY KEY ((collection_id, algorithm_id), event_time)"
                + ") WITH CLUSTERING ORDER BY (event_time DESC);");
    }

    @Override
    public void createBanditSet(String banditSetId, Set<String> banditIds) {
        PreparedStatement statement = session.prepare(
                "INSERT INTO " + keyspace + ".collection "
                + "(collection_id, algorithm_set) "
                + "VALUES (?, ?);");

        BoundStatement boundStatement = new BoundStatement(statement);
        session.execute(boundStatement.bind(
                banditSetId,
                banditIds));

        createBanditsInCollection(banditSetId, banditIds);
    }
    
    @Override
    public void createBanditSuperSet(String banditSuperSetId, Set<String> collectionIds) {
        PreparedStatement statement = session.prepare(
                "INSERT INTO " + keyspace + ".supercollection "
                + "(supercollection_id, collection_set) "
                + "VALUES (?, ?);");
        BoundStatement boundStatement = new BoundStatement(statement);
        session.execute(boundStatement.bind(
                banditSuperSetId,
                collectionIds));
    }    

    private void createBanditsInCollection(String banditSetId, Set<String> banditIds) {
        PreparedStatement statement = session.prepare(
                "INSERT INTO " + keyspace + ".algorithm "
                + "(collection_id, algorithm_id, event_time, probability_in_time, trials_rate, successes_rate) "
                + "VALUES (?, ?, ?, ?, ?, ?);");

        Date actualDate = Calendar.getInstance().getTime();
        
        int banditsCount = MathUtil.countBandits(banditIds);
        double initialProbabilityRate = (double) 1 / (double) banditsCount;

        Iterator<String> bandits = banditIds.iterator();
        while (bandits.hasNext()) {
            String banditId = bandits.next();

            BoundStatement boundStatement = new BoundStatement(statement);
            session.execute(boundStatement.bind(
                    banditSetId,
                    banditId,
                    actualDate,
                    initialProbabilityRate,
                    0.0,
                    0.0));
            logger.info("Creating bandit with ID " + banditId + " for collection : " + banditSetId);
        }
    }

    public void close() {
        cluster.close();
    }

    @Override
    public List<BayesianStrategy> loadLastConfiguration(Environment env) {
        List<BayesianStrategy> strategies = new ArrayList<>();

        try {
            //ResultSet results = session.execute("SELECT * FROM " + keyspace + ".collection WHERE collection_id='" + env.getProperty("ensemble.default.collection") + "';");

            ResultSet results = session.execute("SELECT * FROM " + keyspace + ".collection;");

            for (Row row : results) {
                BanditsMachine machine = new BanditsMachine(new ArrayList<Bandit>(), env);
                BayesianStrategy strategy = new BayesianStrategy(row.getString("collection_id"), machine);
                
                Set<String> algorithms = row.getSet("algorithm_set", String.class);

                Iterator<String> iterator = algorithms.iterator();
                while (iterator.hasNext()) {
                    String setElement = iterator.next();
                    ResultSet resultsAlg = session.execute("SELECT * FROM " + keyspace + ".algorithm WHERE collection_id='" + row.getString("collection_id") + "' AND algorithm_id='" + setElement + "' LIMIT 1;");

                    for (Row r : resultsAlg) {
                        Bandit b = new Bandit(r.getDouble("probability_in_time"), r.getString("algorithm_id"), r.getDouble("trials_rate"), r.getDouble("successes_rate"));
                        machine.addBanditToMachine(b);
                    }
                }
                strategies.add(strategy);
            }
        } catch (InvalidQueryException ex) {
            logger.error(ex);
        }

        return strategies;
    }

    @Override
    public void saveCurrentState(List<BayesianStrategy> strategies) {
        if (strategies != null) {
        for (BayesianStrategy strategy : strategies) {
            List<Bandit> bandits = strategy.getBanditsMachine().getBanditList();
            if (bandits.size() > 0) {
                PreparedStatement statement = session.prepare(
                        "INSERT INTO " + keyspace + ".algorithm "
                        + "(collection_id, algorithm_id, event_time, probability_in_time, trials_rate, successes_rate) "
                        + "VALUES (?, ?, ?, ?, ?, ?);");

                Date actualDate = Calendar.getInstance().getTime();

                for (Bandit b : bandits) {
                    BoundStatement boundStatement = new BoundStatement(statement);
                    session.execute(boundStatement.bind(
                            strategy.getCollectionId(),
                            b.getName(),
                            actualDate,
                            b.getProbability(),
                            b.getTrials(),
                            b.getSuccesses()));
                    logger.info("Time: " + actualDate + "Saving bandit with ID " + b.getName() + " into collection : " + strategy.getCollectionId());
                }
            } else {
                logger.info("Cannot persist bandits because there is no bandit in collection with ID: " + strategy.getCollectionId());
            }
        }
        } else {
            logger.info("Nothing to persist because there are no strategies in database.");
        }
    }
}
