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
import cz.cvut.fit.bouchja1.ensemble.bandits.SuperBayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.bandits.util.MathUtil;
import cz.cvut.fit.bouchja1.ensemble.operation.object.LastEnsembleConfiguration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
                + "supercollection_id int,"
                + "supercollection_name text,"
                + "collection_set set<int>,"
                + "PRIMARY KEY (supercollection_id)"
                + ");");


        session.execute(
                "CREATE TABLE IF NOT EXISTS " + keyspace + ".collection ("
                + "collection_id int,"
                + "collection_name text,"
                + "algorithm_set set<int>,"
                + "PRIMARY KEY (collection_id)"
                + ");");

        session.execute(
                "CREATE TABLE IF NOT EXISTS " + keyspace + ".algorithm ("
                + "collection_id int,"
                + "algorithm_id int,"
                + "algorithm_name text,"                
                + "event_time timestamp,"
                //+ "trials_in_time double,"
                //+ "success_in_time double,"
                + "trials_rate double,"
                + "successes_rate double,"
                + "PRIMARY KEY ((collection_id, algorithm_id), event_time)"
                + ") WITH CLUSTERING ORDER BY (event_time DESC);");
    }

    @Override
    public void createBanditSet(int banditSetId, String banditSetName, Set<String> banditIds) {
        PreparedStatement statement = session.prepare(
                "INSERT INTO " + keyspace + ".collection "
                + "(collection_id, collection_name, algorithm_set) "
                + "VALUES (?, ?, ?);");

        Set<Integer> banditIdsKeys = new HashSet<>();
        for (int i = 1; i <= banditIds.size(); i++) {
            banditIdsKeys.add(i);
        }
        
        BoundStatement boundStatement = new BoundStatement(statement);
        session.execute(boundStatement.bind(
                banditSetId,
                banditSetName,
                banditIdsKeys));

        createBanditsInCollection(banditSetId, banditIds);
    }

    @Override
    public void createBanditSuperSet(int banditSuperSetId, String banditSuperSetName, Set<Integer> collectionIds) {
        PreparedStatement statement = session.prepare(
                "INSERT INTO " + keyspace + ".supercollection "
                + "(supercollection_id, supercollection_name, collection_set) "
                + "VALUES (?, ?, ?);");
        BoundStatement boundStatement = new BoundStatement(statement);
        session.execute(boundStatement.bind(
                banditSuperSetId,
                banditSuperSetName,
                collectionIds));
    }

    private void createBanditsInCollection(int banditSetId, Set<String> banditIds) {
        PreparedStatement statement = session.prepare(
                "INSERT INTO " + keyspace + ".algorithm "
                //+ "(collection_id, algorithm_id, algorithm_name, event_time, trials_in_time, success_in_time, trials_rate, successes_rate) "
                + "(collection_id, algorithm_id, algorithm_name, event_time, trials_rate, successes_rate) "
                + "VALUES (?, ?, ?, ?, ?, ?);");

        Date actualDate = Calendar.getInstance().getTime();

        int banditsCount = MathUtil.countBandits(banditIds);
        double initialTrialsFreqRate = (double) 1 / (double) banditsCount;
        double initialSuccessesFreqRate = (double) 1 / (double) banditsCount;

        Iterator<String> bandits = banditIds.iterator();
        int id = 1;
        while (bandits.hasNext()) {
            String banditId = bandits.next();

            BoundStatement boundStatement = new BoundStatement(statement);
            session.execute(boundStatement.bind(
                    banditSetId,
                    id++,
                    banditId,
                    actualDate,
                    //initialTrialsFreqRate,
                    //initialSuccessesFreqRate,
                    0.0,
                    0.0));
            logger.info("Creating bandit with ID " + id + " and name " + banditId + " for collection : " + banditSetId);
        }
    }

    public void close() {
        cluster.close();
    }

    @Override
    public LastEnsembleConfiguration loadLastConfiguration(Environment env) {
        Map<Integer, BayesianStrategy> strategies = new LinkedHashMap<>();
        Map<Integer, SuperBayesianStrategy> superstrategies = new LinkedHashMap<>();
        LastEnsembleConfiguration lastConfiguration = new LastEnsembleConfiguration();

        try {
            //ResultSet results = session.execute("SELECT * FROM " + keyspace + ".collection WHERE collection_id='" + env.getProperty("ensemble.default.collection") + "';");

            ResultSet results = session.execute("SELECT * FROM " + keyspace + ".collection;");

            for (Row row : results) {
                //BanditsMachine machine = new BanditsMachine(new ArrayList<Bandit>(), env);
                BanditsMachine machine = new BanditsMachine(new HashMap<Integer, Bandit>(), env);
                BayesianStrategy strategy = new BayesianStrategy(row.getInt("collection_id"), row.getString("collection_name"), machine);

                Set<Integer> algorithms = row.getSet("algorithm_set", Integer.class);

                Iterator<Integer> iterator = algorithms.iterator();
                while (iterator.hasNext()) {
                    Integer setElement = iterator.next();
                    ResultSet resultsAlg = session.execute("SELECT * FROM " + keyspace + ".algorithm WHERE collection_id=" + row.getInt("collection_id") + " AND algorithm_id=" + setElement + " LIMIT 1;");

                    for (Row r : resultsAlg) {
                        //Bandit b = new Bandit(r.getDouble("trials_in_time"), r.getDouble("success_in_time"), r.getString("algorithm_name"), r.getInt("algorithm_id"), r.getDouble("trials_rate"), r.getDouble("successes_rate"));
                        Bandit b = new Bandit(r.getString("algorithm_name"), r.getInt("algorithm_id"), r.getDouble("trials_rate"), r.getDouble("successes_rate"));
                        machine.addBanditToMachine(b);
                    }
                }
                strategies.put(strategy.getId(), strategy);
            }
            lastConfiguration.setStrategies(strategies);
        } catch (InvalidQueryException ex) {
            logger.error(ex);
        }

        try {
            ResultSet results = session.execute("SELECT * FROM " + keyspace + ".supercollection;");

            for (Row row : results) {
                Set<BayesianStrategy> existingStrategyList = new HashSet<>();
                SuperBayesianStrategy superstrategy = new SuperBayesianStrategy(row.getInt("supercollection_id"), row.getString("supercollection_name"), existingStrategyList);
                Set<Integer> contextCollections = row.getSet("collection_set", Integer.class);

                Iterator<Integer> iterator = contextCollections.iterator();
                while (iterator.hasNext()) {
                    Integer setElement = iterator.next();
                    BayesianStrategy existingStrategy = findStrategy(setElement, strategies);
                    if (existingStrategy != null) {
                        superstrategy.addStrategyToSuperstrategy(existingStrategy);
                    }
                }
                superstrategies.put(superstrategy.getId(), superstrategy);
            }
            lastConfiguration.setSuperStrategies(superstrategies);
        } catch (InvalidQueryException ex) {
            logger.error(ex);
        }

        return lastConfiguration;
    }

@Override
public void saveCurrentState(LastEnsembleConfiguration strategies) {
    if (strategies != null) {
        for (BayesianStrategy strategy : strategies.getStrategies().values()) {
            //List<Bandit> bandits = strategy.getBanditsMachine().getBanditList();
            Map<Integer, Bandit> bandits = strategy.getBanditsMachine().getBanditList();
            if (bandits.size() > 0) {
                /*
                PreparedStatement statement = session.prepare(
                        "INSERT INTO " + keyspace + ".algorithm "
                        + "(collection_id, algorithm_id, algorithm_name, event_time, trials_in_time, success_in_time, trials_rate, successes_rate) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

                */

                PreparedStatement statement = session.prepare(
                        "INSERT INTO " + keyspace + ".algorithm "
                        + "(collection_id, algorithm_id, algorithm_name, event_time, trials_rate, successes_rate) "
                        + "VALUES (?, ?, ?, ?, ?, ?);");

                Date actualDate = Calendar.getInstance().getTime();

                for (Map.Entry<Integer, Bandit> entry : bandits.entrySet()) {
                    BoundStatement boundStatement = new BoundStatement(statement);
                    session.execute(boundStatement.bind(
                            strategy.getId(),
                            entry.getValue().getId(),
                            entry.getValue().getName(),
                            actualDate,
                            //entry.getValue().getNormalizedTrialsFrequencyInTime(),
                            //entry.getValue().getNormalizedSuccessFrequencyInTime(),
                            entry.getValue().getTrials(),
                            entry.getValue().getSuccesses()));
                    logger.info("Time: " + actualDate + "Saving bandit with ID " + entry.getValue().getName() + " into collection : " + strategy.getCollectionId());
                }

            } else {
                logger.info("Cannot persist bandits because there is no bandit in collection with ID: " + strategy.getCollectionId());
            }
        }
    } else {
        logger.info("Nothing to persist because there are no strategies in database.");
    }
}

    private BayesianStrategy findStrategy(Integer strategyId, Map<Integer, BayesianStrategy> strategies) {
        for (BayesianStrategy strategy : strategies.values()) {
            if (strategy.getId() == strategyId) {
                return strategy;
            }
        }
        return null;
    }
}
