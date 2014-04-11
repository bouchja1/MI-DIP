/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.api;

import cz.cvut.fit.bouchja1.ensemble.storage.CassandraStorage;
import cz.cvut.fit.bouchja1.ensemble.storage.IStorage;
import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;
import java.util.List;
import java.util.Set;
import org.springframework.core.env.Environment;

/**
 *
 * @author jan
 */
public interface EnsembleApiFacade {

    public Reply createBanditSet(String banditSetId, Set<String> banditIds);
    public Reply detectBestBandit(String banditCollectionId);
    public Reply selectBandit(String banditCollectionId, String banditId);
    public void setStrategies(List<BayesianStrategy> strategies);
    public void setStorage(IStorage storage);
    public Reply calculateFeedback(String banditCollectionId, String banditId, int feedback);
    public void setEnvironment(Environment env);
    
}
