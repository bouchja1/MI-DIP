/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.message;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import cz.cvut.fit.bouchja1.ensemble.exception.MessageFormatException;
import cz.cvut.fit.bouchja1.ensemble.operation.Operation;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationCreateBanditCollection;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationCreateBanditSuperCollection;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationDetectBestBandit;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationFeedbackBandit;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationGetAllCollections;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationGetAllSuperCollections;
import cz.cvut.fit.bouchja1.ensemble.operation.OperationSelectBandit;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                int collectionToDetect = -1;
                String filter = "";

                while (matcher.find()) {
                    //System.out.println(matcher.group(1));
                    collectionToDetect = Integer.parseInt(matcher.group(1));
                    if (matcher.group(2) != null) {
                        filter = matcher.group(2);
                    }
                }
                if ((collectionToDetect > -1) && (("all".equals(filter)) || ("best".equals(filter)) || ("super".equals(filter)))) {
                    operation = new OperationDetectBestBandit();
                    operationParamMap.put("collectionId", collectionToDetect + "");
                    operationParamMap.put("filter", filter);
                    operation.parseParameters(operationParamMap);
                } else if (requestPath.equals("/ensemble/services/collection")) {
                    operation = new OperationGetAllCollections();
                } else if (requestPath.equals("/ensemble/services/supercollection")) {
                    operation = new OperationGetAllSuperCollections();
                }
                break;
            case "PUT":
                Pattern patternOperation = Pattern.compile("/ensemble/services/collection/(\\d+)/(\\d+)");
                Matcher matcherOperation = patternOperation.matcher(requestPath);
                int collectionToDetectOperation = -1;
                int banditToDetectOperation = -1;
                while (matcherOperation.find()) {
                    //System.out.println(matcher.group(1));
                    collectionToDetectOperation = Integer.parseInt(matcherOperation.group(1));
                    banditToDetectOperation = Integer.parseInt(matcherOperation.group(2));
                }

                if ((collectionToDetectOperation > -1) && (banditToDetectOperation > -1)) {
                    if (requestBody.contains("pull")) {
                        operation = new OperationSelectBandit();
                    } else {
                        Pattern patternFeedback = Pattern.compile("value=(\\d+)");
                        Matcher matcherFeedback = patternFeedback.matcher(requestPath);
                        int feedbackValue = -1;

                        while (matcherFeedback.find()) {
                            //System.out.println(matcher.group(1));
                            feedbackValue = Integer.parseInt(matcherFeedback.group(1));
                        }

                        if (feedbackValue > -1) {
                            operation = new OperationFeedbackBandit();
                            operationParamMap.put("feedbackValue", feedbackValue + "");
                        }
                    }
                    operationParamMap.put("collectionId", collectionToDetectOperation + "");
                    operationParamMap.put("banditId", banditToDetectOperation + "");
                    operation.parseParameters(operationParamMap);
                }
                break;
        }
        return operation;
    }
}
