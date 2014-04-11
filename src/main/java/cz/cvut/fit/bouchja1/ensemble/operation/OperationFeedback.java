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
public class OperationFeedback extends AbstractOperation {
    
    private String banditCollectionId;
    private String banditId;  
    private int feedback;

    @Override
    public Reply executeOperation(EnsembleApiFacade api) {
        return api.calculateFeedback(banditCollectionId, banditId, feedback);
    }

    @Override
    public boolean validateOperation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void parseParameters(Map<String, String> parameters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
