/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.zeromq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditCollection;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditSuperCollection;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditId;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.EnsembleOperation;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.zeromq.SmileRequest;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.zeromq.SmileResponse;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.CommonEndpointHelper;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

/**
 *
 * @author jan
 */
@Component
public class EnsembleZeroMqHelper extends CommonEndpointHelper {
    
    private final Log logger = LogFactory.getLog(getClass());

    public EnsembleZeroMqHelper() {
    }

    //curl -X POST -H "Content-Type: application/json" -d '{"banditSetId":"1","banditIds": [ { "id":"1" }, { "id":"2" }, { "id":"3" }]}' 'http://localhost:8089/ensembleRestApi/recommeng/ensemble/collection'
    public Response createBanditSet(BanditCollection banditCollection) {
        Response resp = null;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(ZMQ.REQ);       
        requester.connect("tcp://localhost:5555");        

        SmileRequest req = new SmileRequest();
        req.setMethod("POST");
        req.setPath("/ensemble/services/collection");
        req.setBody("collectionId=" + banditCollection.getId() + "&bandits=" + formatBanditIds(banditCollection.getBanditIds()));        

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            requester.send(json.getBytes(), 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            logger.info(json);
            
            resp = buildResponse(new String(reply));            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }

        //disconnect(context, requester);
        return resp;
    }
    
    public Response createBanditSuperSet(BanditSuperCollection banditSuperCollection) {
        Response resp = null;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(ZMQ.REQ);       
        requester.connect("tcp://localhost:5555"); 

        SmileRequest req = new SmileRequest();
        req.setMethod("POST");
        req.setPath("/ensemble/services/supercollection");
        req.setBody("supercollectionId=" + banditSuperCollection.getId() + "&collections=" + formatCollectionsId(banditSuperCollection.getContextCollections()));        

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            requester.send(json.getBytes(), 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            logger.info(json);
            
            resp = buildResponse(new String(reply));            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }

        //disconnect(context, requester);
        return resp;
    }    
    
//curl -i -H "Accept: application/json" -H "Content-Type: application/json" 'http://localhost:8089/ensembleRestApi/recommeng/ensemble/collection/1'    
    public Response filterBanditCollection(String collectionId, String filter) {
        //Filter slouzi k tomu, ze se muze treba zadat filter=best a vrati to toho nejvhodnejsiho
        Response resp = null;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(ZMQ.REQ);       
        requester.connect("tcp://localhost:5555"); 

        SmileRequest req = new SmileRequest();
        req.setMethod("GET");
        
        if (filter == null) {
            filter = "all";
        } else if (!"best".equals(filter)) {
            return getBadRequestResponse("Bad value for filter. Supported filters are: best");
        }
        
        req.setPath("/ensemble/services/collection/" + collectionId + "?filter=" + filter);
        
        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            requester.send(json.getBytes(), 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            logger.info(json);
            
            resp = buildResponse(new String(reply));            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        
        //disconnect(context, requester);
        return resp;        
    }
    
    public Response getAllBanditSuperCollections() {
        Response resp = null;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(ZMQ.REQ);       
        requester.connect("tcp://localhost:5555"); 

        SmileRequest req = new SmileRequest();
        req.setMethod("GET");
        
        req.setPath("/ensemble/services/supercollection");
        
        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            requester.send(json.getBytes(), 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            logger.info(json);
            
            resp = buildResponse(new String(reply));            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        
        //disconnect(context, requester);
        return resp; 
    }    
    
    public Response getAllBanditCollections() {
        Response resp = null;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(ZMQ.REQ);       
        requester.connect("tcp://localhost:5555"); 

        SmileRequest req = new SmileRequest();
        req.setMethod("GET");
        
        req.setPath("/ensemble/services/collection");
        
        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            requester.send(json.getBytes(), 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            logger.info(json);
            
            resp = buildResponse(new String(reply));            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        
        //disconnect(context, requester);
        return resp; 
    }

    public Response getBestBanditSuperCollection(String supercollectionId) {
        Response resp = null;
                ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(ZMQ.REQ);       
        requester.connect("tcp://localhost:5555"); 

        SmileRequest req = new SmileRequest();
        req.setMethod("GET");
        
        req.setPath("/ensemble/services/supercollection/" + supercollectionId + "?filter=super");
        
        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            requester.send(json.getBytes(), 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            logger.info(json);
            
            resp = buildResponse(new String(reply));            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        
        //disconnect(context, requester);
        return resp;
    }    
    
    public Response sendEnsembleOperation(String collectionId, String banditId, EnsembleOperation ensembleOperation) {
        Response resp = null;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(ZMQ.REQ);       
        requester.connect("tcp://localhost:5555"); 

        SmileRequest req = new SmileRequest();
        req.setMethod("PUT");
        req.setPath("/ensemble/services/collection/" + collectionId + "/" + banditId);
        
        if ("pull".equals(ensembleOperation.getOperation())) {
            req.setBody("operation=" + ensembleOperation.getOperation());        
        } else if ("feedback".equals(ensembleOperation.getOperation())) {
            req.setBody("operation=" + ensembleOperation.getOperation() + "&value=" + ensembleOperation.getValue()); 
        } else {
            resp = getBadRequestResponse("You entered non-valid operation. Allowed operation are: pull | feedback");
            return resp;
        }  
        
        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            requester.send(json.getBytes(), 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            
            resp = buildResponse(new String(reply));            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }       
        
        //disconnect(context, requester);
        return resp;

    } 

    private void disconnect(ZMQ.Context context, ZMQ.Socket requester) {
        requester.close();
        context.term();
    }

    private String formatBanditIds(Set<BanditId> banditIds) {
        StringBuilder builder = new StringBuilder();
        int banditIdsSize = banditIds.size();
        Iterator<BanditId> bi = banditIds.iterator();
        
        int counter = 1;
        while (bi.hasNext()) {
            builder.append(bi.next().getId());
            if (counter != banditIdsSize) {
                builder.append(",");
            }  
            counter++;
        }
        return builder.toString();
    }
    
    private String formatCollectionsId(Set<BanditCollection> banditContextIds) {
        StringBuilder builder = new StringBuilder();
        int banditIdsSize = banditContextIds.size();
        Iterator<BanditCollection> bi = banditContextIds.iterator();
        
        int counter = 1;
        while (bi.hasNext()) {
            builder.append(bi.next().getId());
            if (counter != banditIdsSize) {
                builder.append(",");
            }  
            counter++;
        }
        return builder.toString();        
    }    

    private Response buildResponse(String result) {
        Response resp;
        //result = result.replaceAll("\"", "\\");
        JSONObject json = new JSONObject(result);
        
        switch ((String)json.get("status")) {
            case "201" : resp = getOkResponse(result);
                break;
            case "400" : resp = getBadRequestResponse(result);
                break;                
            default : resp = getServerError(null);
        }
        return resp;
    }

}
