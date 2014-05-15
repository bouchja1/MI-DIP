/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.api;

import cz.cvut.fit.bouchja1.ensemble.storage.IStorage;
import cz.cvut.fit.bouchja1.ensemble.bandits.SuperBayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;
import cz.cvut.fit.bouchja1.ensemble.operation.object.LastEnsembleConfiguration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.env.Environment;

/**
 *
 * @author jan
 */
public interface EnsembleApiFacade {

    public Reply createBanditSet(String banditSetId, Set<String> banditIds);
    public Reply detectBestBandit(String banditCollectionId, String filter);
    public Reply detectBestSuperBandit(String banditCollectionId, String filter);
    public Reply useBanditFromCollection(String banditCollectionId, String banditId);
    public void setLastConfiguration(LastEnsembleConfiguration strategies);
    public void setStorage(IStorage storage);
    public Reply recalculateFeedbackCollection(String banditCollectionId, String banditId, String feedbackValue);
    public void setEnvironment(Environment env);

    public void setSetOfStrategies(Map<Integer, SuperBayesianStrategy> superStrategies);    
    public Reply createBanditSuperSet(String banditSuperCollectionId, Set<String> collectionIds);
    public void setAllowedBanditsValues(List<String> allowedBanditsValues);
    public Reply getAllCollections();

    public Reply getAllSuperCollections();

    public Reply recalculateFeedbackSupercollection(String banditCollectionId, String banditId, String feedbackValue);

    public Reply useBanditFromSupercollection(String banditCollectionId, String banditId);
    
}
