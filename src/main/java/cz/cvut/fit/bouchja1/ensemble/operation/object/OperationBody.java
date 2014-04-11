/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.operation.object;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jan
 */
public class OperationBody {
    
    private String operationName;
    private Map<String, String> parameters;

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    
}
