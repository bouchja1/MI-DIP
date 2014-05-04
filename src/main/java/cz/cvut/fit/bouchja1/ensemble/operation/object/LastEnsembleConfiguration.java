/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.operation.object;

import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.bandits.SuperBayesianStrategy;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jan
 */
public class LastEnsembleConfiguration {
    private List<BayesianStrategy> contextCollections;
    private Set<SuperBayesianStrategy> superStrategies;

    public List<BayesianStrategy> getContextCollections() {
        return contextCollections;
    }

    public void setContextCollections(List<BayesianStrategy> contextCollections) {
        this.contextCollections = contextCollections;
    }

    public Set<SuperBayesianStrategy> getSuperStrategies() {
        return superStrategies;
    }

    public void setSuperStrategies(Set<SuperBayesianStrategy> superStrategies) {
        this.superStrategies = superStrategies;
    }
    
    
}
