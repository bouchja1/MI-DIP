/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.storage;

import cz.cvut.fit.bouchja1.ensemble.bandits.Bandit;
import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.bandits.SuperBayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.operation.object.LastEnsembleConfiguration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;

/**
 *
 * @author jan
 */
public class JvmStorage implements IStorage {
    
    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void saveCurrentState(LastEnsembleConfiguration strategies) {
        logger.debug("Nothing to save to be persistent - program uses JVM to store information.");
    }

    @Override
    public LastEnsembleConfiguration loadLastConfiguration(Environment env) {
        Set<SuperBayesianStrategy> superstrategy = new HashSet<>();
        List<BayesianStrategy> strategies = new ArrayList<>();
        LastEnsembleConfiguration emptyConfiguration = new LastEnsembleConfiguration();
        emptyConfiguration.setContextCollections(strategies);
        emptyConfiguration.setSuperStrategies(superstrategy);
        return emptyConfiguration;
    }

    @Override
    public void createBanditSet(String banditSetId, Set<String> banditIds) {
        logger.debug("Nothing to create to be persistent - program uses JVM to store information.");
    }

    @Override
    public void createBanditSuperSet(String banditSuperSetId, Set<String> collectionIds) {
        logger.debug("Nothing to create to be persistent - program uses JVM to store information.");
    }
    
}
