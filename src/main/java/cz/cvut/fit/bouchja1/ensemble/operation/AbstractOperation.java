/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.operation;

/**
 *
 * @author jan
 */
public abstract class AbstractOperation implements Operation {
    protected String errorMessage;

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    
    public String getErrorMessage() {
        return errorMessage;
    }    
}
