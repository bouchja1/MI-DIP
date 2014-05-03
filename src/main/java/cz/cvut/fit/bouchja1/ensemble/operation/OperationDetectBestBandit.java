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
public class OperationDetectBestBandit extends AbstractOperation {
    
    private String banditCollectionId;    
    private String filter;

    @Override
    public Reply executeOperation(EnsembleApiFacade api) {
        return api.detectBestBandit(banditCollectionId, filter);
    }

    @Override
    public boolean validateOperation() {
        if ((banditCollectionId == null) || (filter == null)) {
            errorMessage = "You need to pass all parameters (empty collection ID).";
            return false;
        }
        return true;
    }

    @Override
    public void parseParameters(Map<String, String> parameters) {
        this.banditCollectionId = parameters.get("collectionId");
        this.filter = parameters.get("filter");
    }
    
}
