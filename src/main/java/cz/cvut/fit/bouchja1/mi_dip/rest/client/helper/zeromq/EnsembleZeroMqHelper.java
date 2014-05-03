/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.zeromq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
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
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

/**
 *
 * @author jan
 */
@Component
public class EnsembleZeroMqHelper extends CommonEndpointHelper {

    private ZMQ.Socket requester;
    private ZMQ.Context context;
    private SmileFactory factory;
    private ObjectMapper mapper;
    
    private final Log logger = LogFactory.getLog(getClass());

    public EnsembleZeroMqHelper() {
        this.factory = new SmileFactory();
        this.mapper = new ObjectMapper(factory);
    }

    //curl -X POST -H "Content-Type: application/json" -d '{"banditSetId":"1","banditIds": [ { "id":"1" }, { "id":"2" }, { "id":"3" }]}' 'http://localhost:8089/ensembleRestApi/recommeng/ensemble/collection'
    public Response createBanditSet(BanditCollection banditCollection) {
        Response resp = null;
        connect();

        SmileRequest req = new SmileRequest();
        req.setMethod("POST");
        req.setPath("/ensemble/services/collection");
        req.setBody("collectionId=" + banditCollection.getBanditCollectionId() + "&bandits=" + formatBanditIds(banditCollection.getBanditIds()));        

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            byte[] smileData = mapper.writeValueAsBytes(req);
            requester.send(smileData, 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            SmileResponse result = mapper.readValue(reply, SmileResponse.class);
            json = new ObjectMapper().writeValueAsString(result);
            logger.info(json);
            
            resp = buildResponse(result);            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }

        //disconnect();
        return resp;
    }
    
    public Response createBanditSuperSet(BanditSuperCollection banditSuperCollection) {
        Response resp = null;
        connect();

        SmileRequest req = new SmileRequest();
        req.setMethod("POST");
        req.setPath("/ensemble/services/supercollection");
        req.setBody("supercollectionId=" + banditSuperCollection.getBanditSuperCollectionId() + "&collections=" + formatCollectionsId(banditSuperCollection.getBanditContextIds()));        

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            byte[] smileData = mapper.writeValueAsBytes(req);
            requester.send(smileData, 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            SmileResponse result = mapper.readValue(reply, SmileResponse.class);
            json = new ObjectMapper().writeValueAsString(result);
            logger.info(json);
            
            resp = buildResponse(result);            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }

        //disconnect();
        return resp;
    }    
    
//curl -i -H "Accept: application/json" -H "Content-Type: application/json" 'http://localhost:8089/ensembleRestApi/recommeng/ensemble/collection/1'    
    public Response filterBanditCollection(String collectionId, String filter) {
        //Filter slouzi k tomu, ze se muze treba zadat filter=best a vrati to toho nejvhodnejsiho
        Response resp = null;
        connect();

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
            byte[] smileData = mapper.writeValueAsBytes(req);
            requester.send(smileData, 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            SmileResponse result = mapper.readValue(reply, SmileResponse.class);
            json = new ObjectMapper().writeValueAsString(result);
            logger.info(json);
            
            resp = buildResponse(result);            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        
        return resp;        
    }
    
    public Response getAllBanditSuperCollections() {
        Response resp = null;
        connect();

        SmileRequest req = new SmileRequest();
        req.setMethod("GET");
        
        req.setPath("/ensemble/services/supercollection");
        
        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            byte[] smileData = mapper.writeValueAsBytes(req);
            requester.send(smileData, 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            SmileResponse result = mapper.readValue(reply, SmileResponse.class);
            json = new ObjectMapper().writeValueAsString(result);
            logger.info(json);
            
            resp = buildResponse(result);            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        
        return resp; 
    }    
    
    public Response getAllBanditCollections() {
        Response resp = null;
        connect();

        SmileRequest req = new SmileRequest();
        req.setMethod("GET");
        
        req.setPath("/ensemble/services/collection");
        
        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            byte[] smileData = mapper.writeValueAsBytes(req);
            requester.send(smileData, 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            SmileResponse result = mapper.readValue(reply, SmileResponse.class);
            json = new ObjectMapper().writeValueAsString(result);
            logger.info(json);
            
            resp = buildResponse(result);            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        
        return resp; 
    }

    public Response getBestBanditSuperCollection(String supercollectionId) {
        Response resp = null;
        connect();

        SmileRequest req = new SmileRequest();
        req.setMethod("GET");
        
        req.setPath("/ensemble/services/supercollection/" + supercollectionId + "?filter=super");
        
        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            //encode data
            byte[] smileData = mapper.writeValueAsBytes(req);
            requester.send(smileData, 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            SmileResponse result = mapper.readValue(reply, SmileResponse.class);
            json = new ObjectMapper().writeValueAsString(result);
            logger.info(json);
            
            resp = buildResponse(result);            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        
        return resp;
    }    
    
    public Response sendEnsembleOperation(String collectionId, String banditId, EnsembleOperation ensembleOperation) {
        Response resp = null;
        connect();

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
            byte[] smileData = mapper.writeValueAsBytes(req);
            requester.send(smileData, 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            SmileResponse result = mapper.readValue(reply, SmileResponse.class);
            json = new ObjectMapper().writeValueAsString(result);
            logger.info(json);
            
            resp = buildResponse(result);            
            
            requester.close();
        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }       
        
        return resp;

    } 

    private void connect() {
        context = ZMQ.context(1);
        requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://localhost:5555");
    }

    private void disconnect() {
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
            builder.append(bi.next().getBanditCollectionId());
            if (counter != banditIdsSize) {
                builder.append(",");
            }  
            counter++;
        }
        return builder.toString();        
    }    

    private Response buildResponse(SmileResponse result) {
        Response resp;
        switch (result.getStatus()) {
            case "201" : resp = getOkResponse(result.getBody());
                break;
            case "400" : resp = getBadRequestResponse(result.getBody());
                break;                
            default : resp = getServerError(null);
        }
        return resp;
    }

}
