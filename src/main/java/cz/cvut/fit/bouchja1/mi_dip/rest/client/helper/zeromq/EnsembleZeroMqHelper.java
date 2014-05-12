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
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.zeromq.EnsembleRequest;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.zeromq.EnsembleResponse;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.CommonEndpointHelper;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

/**
 *
 * @author jan
 */
@Component
public class EnsembleZeroMqHelper extends CommonEndpointHelper {

    private final Log logger = LogFactory.getLog(getClass());

    private static class ClientTask implements Callable<String> {

        private String json;

        public ClientTask(String json) {
            this.json = json;
        }

        @Override
        public String call() throws Exception {
            ZContext ctx = new ZContext();
            Socket client = ctx.createSocket(ZMQ.DEALER);
            client.connect("tcp://localhost:5555");
            String replyString = "";

            client.send(json.getBytes(), 0);

            ZMsg msg = ZMsg.recvMsg(client);
            ZFrame content = msg.pop();

            assert (content != null);

            byte[] reply = content.getData();
            System.out.println("Received reply: ["
                    + new String(reply) //  Creates a String from request, minus the last byte
                    + "]");

            replyString = new String(reply);

            msg.destroy();
            //}

            ctx.destroy();
            return replyString;
        }
    }

    public EnsembleZeroMqHelper() {
    }

    //curl -X POST -H "Content-Type: application/json" -d '{"banditSetId":"1","banditIds": [ { "id":"1" }, { "id":"2" }, { "id":"3" }]}' 'http://localhost:8089/ensembleRestApi/recommeng/ensemble/collection'
    public Response createBanditSet(BanditCollection banditCollection) {
        Response resp = null;
        ZContext ctx = new ZContext();

        EnsembleRequest req = new EnsembleRequest();
        req.setMethod("POST");
        req.setPath("/ensemble/services/collection");
        req.setBody("collectionId=" + banditCollection.getName() + "&bandits=" + formatBanditIds(banditCollection.getBanditIds()));

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> result = executor.submit(new ClientTask(json));

            try {
                resp = buildResponse(result.get());
            } catch (Exception ex) {
                resp = getServerError(ex.getMessage());
            }

        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        ctx.destroy();
        return resp;
    }

    public Response createBanditSuperSet(BanditSuperCollection banditSuperCollection) {
        Response resp = null;
        ZContext ctx = new ZContext();

        EnsembleRequest req = new EnsembleRequest();
        req.setMethod("POST");
        req.setPath("/ensemble/services/supercollection");
        req.setBody("supercollectionId=" + banditSuperCollection.getName() + "&collections=" + formatCollectionsId(banditSuperCollection.getContextCollections()));

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> result = executor.submit(new ClientTask(json));

            try {
                resp = buildResponse(result.get());
            } catch (Exception ex) {
                resp = getServerError(ex.getMessage());
            }

        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        ctx.destroy();
        return resp;
    }

//curl -i -H "Accept: application/json" -H "Content-Type: application/json" 'http://localhost:8089/ensembleRestApi/recommeng/ensemble/collection/1'    
    public Response filterBanditCollection(String collectionId, String filter) {
        //Filter slouzi k tomu, ze se muze treba zadat filter=best a vrati to toho nejvhodnejsiho
        Response resp = null;
        ZContext ctx = new ZContext();

        EnsembleRequest req = new EnsembleRequest();
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

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> result = executor.submit(new ClientTask(json));

            try {
                resp = buildResponse(result.get());
            } catch (Exception ex) {
                resp = getServerError(ex.getMessage());
            }

        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        ctx.destroy();
        return resp;
    }

    public Response getAllBanditSuperCollections() {
        Response resp = null;
        ZContext ctx = new ZContext();

        EnsembleRequest req = new EnsembleRequest();
        req.setMethod("GET");

        req.setPath("/ensemble/services/supercollection");

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> result = executor.submit(new ClientTask(json));

            try {
                resp = buildResponse(result.get());
            } catch (Exception ex) {
                resp = getServerError(ex.getMessage());
            }

        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        ctx.destroy();
        return resp;
    }

    public Response getAllBanditCollections() {
        Response resp = null;
        ZContext ctx = new ZContext();

        EnsembleRequest req = new EnsembleRequest();
        req.setMethod("GET");

        req.setPath("/ensemble/services/collection");

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> result = executor.submit(new ClientTask(json));

            try {
                resp = buildResponse(result.get());
            } catch (Exception ex) {
                resp = getServerError(ex.getMessage());
            }

        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        ctx.destroy();
        return resp;
    }

    public Response filterBestBanditSuperCollection(String supercollectionId, String filter) {
        Response resp = null;
        ZContext ctx = new ZContext();

        EnsembleRequest req = new EnsembleRequest();
        req.setMethod("GET");

        if (filter == null) {
            filter = "all";
        } else if (!"best".equals(filter)) {
            return getBadRequestResponse("Bad value for filter. Supported filters are: best");
        }

        req.setPath("/ensemble/services/supercollection/" + supercollectionId + "?filter=" + filter);

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> result = executor.submit(new ClientTask(json));

            try {
                resp = buildResponse(result.get());
            } catch (Exception ex) {
                resp = getServerError(ex.getMessage());
            }

        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        ctx.destroy();
        return resp;
    }

    public Response sendEnsembleOperationToCollection(String collectionId, EnsembleOperation ensembleOperation) {
        Response resp = null;
        ZContext ctx = new ZContext();

        EnsembleRequest req = new EnsembleRequest();
        req.setMethod("PUT");

        //kontrola pritomnosti operace a rozhodnuti o ni
        if ((ensembleOperation.getOperation() == null) || (ensembleOperation.getBandit() == 0)) {
            resp = getBadRequestResponse("You need to specify bandit name and operation to be provided on the bandit.");
            return resp;
        } else {
            if ("feedback".equals(ensembleOperation.getOperation())) {
                if (("possitive".equals(ensembleOperation.getFeedbackType())) || ("negative".equals(ensembleOperation.getFeedbackType()))) {
                    req.setBody("operation=" + ensembleOperation.getOperation() + "&feedbackType=" + ensembleOperation.getFeedbackType());
                } else {
                    resp = getBadRequestResponse("You need to specify a type of feedback: possitive or negative.");
                    return resp;
                }
            } else if (!"use".equals(ensembleOperation.getOperation())) {
                resp = getBadRequestResponse("Unknown type of operation.");
                return resp;
            } else {
                req.setBody("operation=" + ensembleOperation.getOperation());
            }
        }

        req.setPath("/ensemble/services/collection/" + collectionId + "/" + ensembleOperation.getBandit());

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> result = executor.submit(new ClientTask(json));

            try {
                resp = buildResponse(result.get());
            } catch (Exception ex) {
                resp = getServerError(ex.getMessage());
            }

        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        ctx.destroy();
        return resp;
    }

    public Response sendEnsembleOperationToSuperCollection(String supercollectionId, EnsembleOperation ensembleOperation) {
        Response resp = null;
        ZContext ctx = new ZContext();

        EnsembleRequest req = new EnsembleRequest();
        req.setMethod("PUT");

        //kontrola pritomnosti operace a rozhodnuti o ni
        if ((ensembleOperation.getOperation() == null) || (ensembleOperation.getBandit() == 0)) {
            resp = getBadRequestResponse("You need to specify bandit name and operation to be provided on the bandit.");
            return resp;
        } else {
            if ("feedback".equals(ensembleOperation.getOperation())) {
                if (("possitive".equals(ensembleOperation.getFeedbackType())) || ("negative".equals(ensembleOperation.getFeedbackType()))) {
                    req.setBody("operation=" + ensembleOperation.getOperation() + "&feedbackType=" + ensembleOperation.getFeedbackType());
                } else {
                    resp = getBadRequestResponse("You need to specify a type of feedback: possitive or negative.");
                    return resp;
                }
            } else if (!"use".equals(ensembleOperation.getOperation())) {
                resp = getBadRequestResponse("Unknown type of operation.");
                return resp;
            } else {
                req.setBody("operation=" + ensembleOperation.getOperation());
            }
        }

        req.setPath("/ensemble/services/supercollection/" + supercollectionId + "/" + ensembleOperation.getBandit());

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            logger.info("Sending to server:");
            logger.info(json);
            logger.info("");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> result = executor.submit(new ClientTask(json));

            try {
                resp = buildResponse(result.get());
            } catch (Exception ex) {
                resp = getServerError(ex.getMessage());
            }

        } catch (JsonProcessingException ex) {
            resp = getBadRequestResponse(ex.getMessage());
        } catch (IOException ex) {
            resp = getServerError(ex.getMessage());
        }
        ctx.destroy();
        return resp;
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

    private String formatCollectionsId(Set<Integer> banditContextIds) {
        StringBuilder builder = new StringBuilder();
        int banditIdsSize = banditContextIds.size();
        Iterator<Integer> bi = banditContextIds.iterator();

        int counter = 1;
        while (bi.hasNext()) {
            builder.append(bi.next());
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

        switch ((int) json.get("status")) {
            case 200:
                resp = getOkResponse(result);
                break;
            case 201:
                boolean superColl = json.isNull("supercollection");
                //boolean coll = json.isNull("collection");
                String identifier;
                int id;
                if (superColl) {
                    identifier = "collection";
                    id = json.getInt("collection");
                } else {
                    identifier = "supercollection";
                    id = json.getInt("supercollection");
                }
                resp = getCreatedResponse(identifier, id);
                break;                
            case 400:
                resp = getBadRequestResponse(result);
                break;
            case 404:
                resp = getNotFoundResponse(result);
                break;
            case 500:
                resp = getServerError(result);
                break;
            default:
                resp = getServerError("Unknown server error.");
        }
        return resp;
    }
}
