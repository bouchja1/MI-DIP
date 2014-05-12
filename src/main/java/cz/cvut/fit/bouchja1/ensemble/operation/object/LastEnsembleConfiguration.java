/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.operation.object;

import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.bandits.SuperBayesianStrategy;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jan
 */
public class LastEnsembleConfiguration {
    private Map<Integer, BayesianStrategy> strategies;
    private Map<Integer, SuperBayesianStrategy> superStrategies;

    public Map<Integer, BayesianStrategy> getStrategies() {
        return strategies;
    }

    public void setStrategies(Map<Integer, BayesianStrategy> strategies) {
        this.strategies = strategies;
    }

    public Map<Integer, SuperBayesianStrategy> getSuperStrategies() {
        return superStrategies;
    }

    public void setSuperStrategies(Map<Integer, SuperBayesianStrategy> superStrategies) {
        this.superStrategies = superStrategies;
    }
       
    
}
