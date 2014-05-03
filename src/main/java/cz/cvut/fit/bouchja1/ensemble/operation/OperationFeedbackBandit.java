/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.operation;

import cz.cvut.fit.bouchja1.ensemble.api.EnsembleApiFacade;
import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;
import java.util.Map;

/**
 *
 * @author jan
 */
public class OperationFeedbackBandit extends AbstractOperation {
    
    private String banditCollectionId;
    private String banditId;  
    private String feedbackValue;  

    @Override
    public Reply executeOperation(EnsembleApiFacade api) {
        return api.calculateFeedback(banditCollectionId, banditId, feedbackValue);
    }

    @Override
    public boolean validateOperation() {
        if ((banditCollectionId == null) || (banditId == null) || (feedbackValue == null)) {
            errorMessage = "You need to pass all parameters (empty collection ID or bandit ID or feedback value).";
            return false;
        }
        return true;
    }

    @Override
    public void parseParameters(Map<String, String> parameters) {
        this.banditCollectionId = parameters.get("collectionId");
        this.banditId = parameters.get("banditId");
        this.feedbackValue = parameters.get("feedbackValue");
    }
    
}
