/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.message;

import cz.cvut.fit.bouchja1.ensemble.exception.MessageFormatException;
import cz.cvut.fit.bouchja1.ensemble.operation.Operation;
import cz.cvut.fit.bouchja1.ensemble.operation.object.OperationBody;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationCreateBanditCollection;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationDetectBestBandit;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationSelectBandit;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jan
 */
public class RequestHandlerDefault implements RequestHandler {
    
    private String message;

    @Override
    public Operation handleMessage(byte[] message) throws MessageFormatException {
        this.message = new String(message);
        OperationBody operationBody = getOperationBody();        
        return chooseOperation(operationBody);
    }

    private OperationBody getOperationBody() throws MessageFormatException {
        if (!message.contains("&")) {
            throw new MessageFormatException("Bad format of request message.");
        }
        String[] messageParameters = message.split("&");        
        Map<String, String> operationParamMap = new HashMap<>();
        
        for (int i=0; i < messageParameters.length;i++) {
            String[] param = messageParameters[i].split("=");
            operationParamMap.put(param[0], param[1]);
        }
        
        if (operationParamMap.isEmpty()) {
            throw new MessageFormatException("Bad format of request message.");
        }
        
        OperationBody body = new OperationBody();
        body.setOperationName(operationParamMap.get("operation"));
        body.setParameters(operationParamMap);
        return body;
    }

    private Operation chooseOperation(OperationBody operationBody) {
        Operation operation = null;
        switch (operationBody.getOperationName()) {
            case "createSet" : {
                    operation = new OperationCreateBanditCollection();
                }
                break;
            case "detect" : {
                    operation = new OperationDetectBestBandit();                    
            }
                break;
            case "recommend" : {
                    operation = new OperationSelectBandit();
            }
                break;
        }
        
        if (operation != null) {
            operation.parseParameters(operationBody.getParameters());
        }
        
        return operation;
    }
    
}
