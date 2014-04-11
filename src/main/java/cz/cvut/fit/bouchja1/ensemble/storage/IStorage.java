/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.storage;

import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import java.util.List;
import java.util.Set;
import org.springframework.core.env.Environment;

/**
 *
 * @author jan
 */
public interface IStorage {

    public void saveCurrentState(List<BayesianStrategy> strategies);

    public List<BayesianStrategy> loadLastConfiguration(Environment env);

    public void createBanditSet(String banditSetId, Set<String> banditIds);
    
}
