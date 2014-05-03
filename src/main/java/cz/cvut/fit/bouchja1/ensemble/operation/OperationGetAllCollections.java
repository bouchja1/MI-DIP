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
public class OperationGetAllCollections extends AbstractOperation {

    @Override
    public Reply executeOperation(EnsembleApiFacade api) {
        return api.getAllCollections();
    }

    @Override
    public boolean validateOperation() {
        return true;
    }

    @Override
    public void parseParameters(Map<String, String> parameters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
