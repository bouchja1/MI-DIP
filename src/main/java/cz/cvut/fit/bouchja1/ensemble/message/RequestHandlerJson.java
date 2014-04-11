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
import cz.cvut.fit.bouchja1.ensemble.operation.OperationDetectBestBandit;
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
                if (result.getPath().equals("/ensemble/services/collection")) {
                    operation = new OperationCreateBanditCollection();

                    if (!result.getBody().contains("&")) {
                        throw new MessageFormatException("Bad format of request message.");
                    }
                    String[] messageParameters = result.getBody().split("&");

                    for (int i = 0; i < messageParameters.length; i++) {
                        String[] param = messageParameters[i].split("=");
                        operationParamMap.put(param[0], param[1]);
                    }                    
                }
                break;
            case "GET":
                Pattern pattern = Pattern.compile("/ensemble/services/collection/(\\d+)");
                Matcher matcher = pattern.matcher(result.getPath());
                int collectionToDetect = -1;
                while (matcher.find()) {
                    //System.out.println(matcher.group(1));
                    collectionToDetect = Integer.parseInt(matcher.group(1));
                }
                if (collectionToDetect > -1) {
                    operation = new OperationDetectBestBandit();
                    operationParamMap.put("collectionId", collectionToDetect+"");
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
