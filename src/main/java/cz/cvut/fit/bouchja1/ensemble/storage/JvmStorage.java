/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.storage;

import cz.cvut.fit.bouchja1.ensemble.bandits.Bandit;
import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import java.util.ArrayList;
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
    public void saveCurrentState(List<BayesianStrategy> strategies) {
        logger.debug("Nothing to save to be persistent - program uses JVM to store information.");
    }

    @Override
    public List<BayesianStrategy> loadLastConfiguration(Environment env) {
        List<BayesianStrategy> strategies = new ArrayList<>();
        return strategies;
    }

    @Override
    public void createBanditSet(String banditSetId, Set<String> banditIds) {
        logger.debug("Nothing to create to be persistent - program uses JVM to store information.");
    }
    
}
