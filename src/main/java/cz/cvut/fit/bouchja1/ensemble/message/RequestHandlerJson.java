package cz.cvut.fit.bouchja1.ensemble.message;

import cz.cvut.fit.bouchja1.ensemble.exception.MessageFormatException;
import cz.cvut.fit.bouchja1.ensemble.operation.Operation;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationCreateBanditCollection;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationCreateBanditSuperCollection;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationDetectBestBandit;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationDetectBestSuperBandit;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationFeedbackCollectionBandit;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationFeedbackSupercollectionBandit;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationGetAllCollections;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationGetAllSuperCollections;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationUseCollectionBandit;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationUseSupercollectionBandit;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 *
 * @author jan
 */
public class RequestHandlerJson implements RequestHandler {

    @Override
    public Operation handleMessage(byte[] message) throws MessageFormatException, IOException {
        Operation operation = null;
        JSONObject json = new JSONObject(new String(message));

        String requestMethod;
        String requestPath;
        String requestBody = null;

        if (json.isNull("method") || json.isNull("path")) {
            throw new MessageFormatException("Bad format of request message. You need to specify METHOD and PATH.");
        } else {
            requestMethod = (String) json.get("method");
            requestPath = (String) json.get("path");
        }

        if (!json.isNull("body")) {
            requestBody = (String) json.get("body");
        }

        Map<String, String> operationParamMap = new HashMap<>();

        switch (requestMethod) {
            case "POST":
                if (requestPath.equals("/ensemble/services/supercollection")) {
                    operation = new OperationCreateBanditSuperCollection();

                    if (requestBody != null) {
                        if (!requestBody.contains("&")) {
                            throw new MessageFormatException("Bad format of request message.");
                        }
                        String[] messageParameters = requestBody.split("&");

                        for (int i = 0; i < messageParameters.length; i++) {
                            String[] param = messageParameters[i].split("=");
                            operationParamMap.put(param[0], param[1]);
                        }
                        operation.parseParameters(operationParamMap);
                    } else {
                        //body je PRAZDNE
                    }
                } else if (requestPath.equals("/ensemble/services/collection")) {
                    operation = new OperationCreateBanditCollection();

                    if (requestBody != null) {
                        if (!requestBody.contains("&")) {
                            throw new MessageFormatException("Bad format of request message.");
                        }
                        String[] messageParameters = requestBody.split("&");

                        for (int i = 0; i < messageParameters.length; i++) {
                            String[] param = messageParameters[i].split("=");
                            operationParamMap.put(param[0], param[1]);
                        }
                        operation.parseParameters(operationParamMap);
                    } else {
                        //vyhod exceptionu, je to prazdny
                    }
                }
                break;
            case "GET":
                Pattern pattern = Pattern.compile("/ensemble/services/(collection|supercollection)/(\\d+)\\?filter=(\\S+)");
                Matcher matcher = pattern.matcher(requestPath);
                String collOrSupercoll = "";
                int collectionToDetect = -1;
                String filter = "";

                while (matcher.find()) {
                    //System.out.println(matcher.group(1));
                    collOrSupercoll = matcher.group(1);
                    collectionToDetect = Integer.valueOf(matcher.group(2));
                    if (matcher.group(3) != null) {
                        filter = matcher.group(3);
                    }
                }

                if ((collectionToDetect > -1) && (("all".equals(filter)) || ("best".equals(filter))) && (("collection".equals(collOrSupercoll)) || ("supercollection".equals(collOrSupercoll)))) {
                    if ("collection".equals(collOrSupercoll)) {
                        operation = new OperationDetectBestBandit();
                    } else if ("supercollection".equals(collOrSupercoll)) {
                        operation = new OperationDetectBestSuperBandit();
                    }
                    operationParamMap.put("collectionId", collectionToDetect + "");
                    operationParamMap.put("filter", filter);
                    operation.parseParameters(operationParamMap);
                } else if (requestPath.equals("/ensemble/services/collection")) {
                    operation = new OperationGetAllCollections();
                } else if (requestPath.equals("/ensemble/services/supercollection")) {
                    operation = new OperationGetAllSuperCollections();
                } else {
                    //ZE TO NEODPOVIDA NICEMU?
                }
                break;
            case "PUT":
                Pattern patternOperation = Pattern.compile("/ensemble/services/(collection|supercollection)/(\\d+)/(\\S+)");
                Matcher matcherOperation = patternOperation.matcher(requestPath);
                String collectionType = "";
                int collectionToDetectOperation = -1;
                String banditToDetectOperation = "";
                while (matcherOperation.find()) {
                    //System.out.println(matcher.group(1));
                    collectionType = matcherOperation.group(1);
                    collectionToDetectOperation = Integer.parseInt(matcherOperation.group(2));
                    banditToDetectOperation = matcherOperation.group(3);
                }

                if ((collectionToDetectOperation > -1) && (!"".equals(banditToDetectOperation)) && (("collection".equals(collectionType)) || ("supercollection".equals(collectionType)))) {                    
                    Pattern patternConcreteOperation = Pattern.compile("operation=(feedback|use)(&feedbackType=(possitive|negative))?");
                    Matcher matcherConcreteOperation = patternConcreteOperation.matcher(requestBody);                   
                    String operationType = "";
                    String feedback = "";
                    String feedbackType = "";
                    while (matcherConcreteOperation.find()) {
                        operationType = matcherConcreteOperation.group(1);
                        feedback = matcherConcreteOperation.group(2);
                        feedbackType = matcherConcreteOperation.group(3);
                    }
                    
                    if ("feedback".equals(operationType)) {
                        if ((!"".equals(feedback)) && (("possitive".equals(feedbackType)) || ("negative".equals(feedbackType)))) {
                            if ("collection".equals(collectionType)) {
                                operation = new OperationFeedbackCollectionBandit();
                                operationParamMap.put("feedbackType", feedbackType);                             
                            } else {
                                operation = new OperationFeedbackSupercollectionBandit();
                                operationParamMap.put("feedbackType", feedbackType);                                    
                            }                       
                        }
                    } else if ("use".equals(operationType)) {
                            if ("collection".equals(collectionType)) {
                                operation = new OperationUseCollectionBandit();
                            } else {
                                operation = new OperationUseSupercollectionBandit();
                            } 
                    } else {
                        //neznamy typ operace
                    }
                    
                    operationParamMap.put("collectionId", collectionToDetectOperation + "");
                    operationParamMap.put("banditId", banditToDetectOperation);
                    operation.parseParameters(operationParamMap);
                } else {
                    //ze chybi ty parametry
                }
                break;
            default: //neco jako Unsupported operation
        }
        return operation;
    }
}
