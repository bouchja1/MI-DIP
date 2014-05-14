package cz.cvut.fit.bouchja1.ensemble.storage;

import cz.cvut.fit.bouchja1.ensemble.bandits.Bandit;
import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.bandits.SuperBayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.operation.object.LastEnsembleConfiguration;
import java.util.ArrayList;
import java.util.HashSet;
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
public class JvmStorage implements IStorage {
    
    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void saveCurrentState(LastEnsembleConfiguration strategies) {
        logger.debug("Nothing to save to be persistent - program uses JVM to store information.");
    }

    @Override
    public LastEnsembleConfiguration loadLastConfiguration(Environment env) {
        Map<Integer, SuperBayesianStrategy> superStrategies = new LinkedHashMap<>();
        Map<Integer, BayesianStrategy> strategies = new LinkedHashMap<>();
        LastEnsembleConfiguration emptyConfiguration = new LastEnsembleConfiguration();
        emptyConfiguration.setStrategies(strategies);
        emptyConfiguration.setSuperStrategies(superStrategies);
        return emptyConfiguration;
    }

    @Override
    public void createBanditSet(int banditSetId, String banditSetName, Set<String> banditIds) {
        logger.debug("Nothing to create to be persistent - program uses JVM to store information.");
    }

    @Override
    public void createBanditSuperSet(int banditSuperSetId, String banditSuperSetName, Set<Integer> collectionIds) {
        logger.debug("Nothing to create to be persistent - program uses JVM to store information.");
    }
    
}
