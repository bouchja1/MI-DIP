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

/**
 *
 * @author jan
 */
public class RequestHandlerJson implements RequestHandler {

    private String method;
    private String path;
    private String body;
    final SmileFactory smileFactory = new SmileFactory();
    final ObjectMapper smileMapper = new ObjectMapper(smileFactory);

    @Override
    public Operation handleMessage(byte[] message) throws MessageFormatException, IOException {
        Operation operation = null;
        RequestHandlerJson result = smileMapper.readValue(message, RequestHandlerJson.class);

        Map<String, String> operationParamMap = new HashMap<>();

        switch (result.getMethod()) {
            case "POST":
                if (result.getPath().equals("/ensemble/services/supercollection")) {
                    operation = new OperationCreateBanditSuperCollection();

                    if (!result.getBody().contains("&")) {
                        throw new MessageFormatException("Bad format of request message.");
                    }
                    String[] messageParameters = result.getBody().split("&");

                    for (int i = 0; i < messageParameters.length; i++) {
                        String[] param = messageParameters[i].split("=");
                        operationParamMap.put(param[0], param[1]);
                    }
                    operation.parseParameters(operationParamMap);
                } else if (result.getPath().equals("/ensemble/services/collection")) {
                    operation = new OperationCreateBanditCollection();

                    if (!result.getBody().contains("&")) {
                        throw new MessageFormatException("Bad format of request message.");
                    }
                    String[] messageParameters = result.getBody().split("&");

                    for (int i = 0; i < messageParameters.length; i++) {
                        String[] param = messageParameters[i].split("=");
                        operationParamMap.put(param[0], param[1]);
                    }
                    operation.parseParameters(operationParamMap);
                }
                break;
            case "GET":
                Pattern pattern = Pattern.compile("/ensemble/services/(collection|supercollection)/(\\d+)\\?filter=(\\S+)");
                Matcher matcher = pattern.matcher(result.getPath());
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
                } else if (result.getPath().equals("/ensemble/services/collection")) {
                    operation = new OperationGetAllCollections();
                } else if (result.getPath().equals("/ensemble/services/supercollection")) {
                    operation = new OperationGetAllSuperCollections();
                }
                break;
            case "PUT":
                Pattern patternOperation = Pattern.compile("/ensemble/services/collection/(\\d+)/(\\d+)");
                Matcher matcherOperation = patternOperation.matcher(result.getPath());
                int collectionToDetectOperation = -1;
                int banditToDetectOperation = -1;
                while (matcherOperation.find()) {
                    //System.out.println(matcher.group(1));
                    collectionToDetectOperation = Integer.parseInt(matcherOperation.group(1));
                    banditToDetectOperation = Integer.parseInt(matcherOperation.group(2));
                }

                if ((collectionToDetectOperation > -1) && (banditToDetectOperation > -1)) {
                    if (result.getBody().contains("pull")) {
                        operation = new OperationSelectBandit();
                    } else {
                        Pattern patternFeedback = Pattern.compile("value=(\\d+)");
                        Matcher matcherFeedback = patternFeedback.matcher(result.getPath());
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
