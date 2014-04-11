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
public interface Operation {
    public Reply executeOperation(EnsembleApiFacade api);
    public boolean validateOperation();
    public void parseParameters(Map<String, String> parameters);
    public String getErrorMessage();
}
