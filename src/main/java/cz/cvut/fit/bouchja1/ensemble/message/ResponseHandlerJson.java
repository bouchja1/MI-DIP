package cz.cvut.fit.bouchja1.ensemble.message;

import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;
import cz.cvut.fit.bouchja1.ensemble.operation.object.ContextCollection;
import cz.cvut.fit.bouchja1.ensemble.operation.object.Supercollection;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author jan
 */
public class ResponseHandlerJson implements ResponseHandler {

    private Reply reply;
    private static final int ERROR_REPLY = 400;
    private static final int INTERNAL_ERROR = 500;
    private static final int SUCCESS_REPLY = 200;
    private static final int CREATED_REPLY = 201;
    private static final int NOT_FOUND_REPLY = 404;

    @Override
    public Reply returnReply() {
        return reply;
    }
    
    @Override
    public String buildReply() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("status", reply.getStatus())
                .add("message", reply.getMessage());
        if (reply.getBestBandit() != -1) {
            builder.add("bestBandit", reply.getBestBandit());
        }        
        if (reply.getCollection() != null) {
            builder.add("collection", reply.getCollection());
        }     
        if (reply.getSuperCollection()!= null) {
            builder.add("supercollection", reply.getSuperCollection());
        }           
        if (reply.getBestBanditIdent() != null) {
            builder.add("banditId", reply.getBestBanditIdent());
        }           
        if (!reply.getContextCollection().isEmpty()) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (ContextCollection coll : reply.getContextCollection()) {
                arrayBuilder.add(coll.getId());
            }
            builder.add("collections", arrayBuilder);
        }
        if (!reply.getSupercollection().isEmpty()) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Supercollection supercoll : reply.getSupercollection()) {
                arrayBuilder.add(supercoll.getId());
                JsonArrayBuilder arrayOfArrayBuilder = Json.createArrayBuilder();
                for (ContextCollection coll : supercoll.getContextCollections()) {
                    arrayOfArrayBuilder.add(coll.getId());
                }
            }
            builder.add("supercollections", arrayBuilder);
        }        

        return builder.build().toString();
    }

    @Override
    public void createSuccessReply(String message) {
        reply = new Reply(SUCCESS_REPLY, message);
    }
    
    @Override
    public void createSuccessReply(String message, String collectionId) {
        reply = new Reply(SUCCESS_REPLY, message, collectionId);
    }    
    
    @Override
    public void createCreatedCollectionReply(String message, String collectionId) {
        reply = new Reply(CREATED_REPLY, message, collectionId, "collection");
    }     
    
    @Override
    public void createCreatedSupercollectionReply(String message, String collectionId) {
        reply = new Reply(CREATED_REPLY, message, collectionId, "supercollection");
    }     
    
    @Override
    public void createSuccessReplyDetection(String message, int bestBandit, String banditIdent, String collection) {
        reply = new Reply(SUCCESS_REPLY, message, bestBandit, banditIdent, collection);
    }       

    @Override
    public void createSuccessReplyCollections(String message, List<ContextCollection> contextCollections) {
        reply = new Reply(SUCCESS_REPLY, message);
        reply.setContextCollection(contextCollections);
    }

    @Override
    public void createSuccessReplySupercollections(String message, List<Supercollection> superCollections) {
        reply = new Reply(SUCCESS_REPLY, message);
        reply.setSupercollection(superCollections);
    }

    @Override
    public void createErrorReply(String message) {
        reply = new Reply(ERROR_REPLY, message);
    }

    @Override
    public void createInternalErrorReply(String message) {
        reply = new Reply(INTERNAL_ERROR, message);
    }

    @Override
    public void setReply(Reply reply) {
        this.reply = reply;
    }

    @Override
    public void createNotFoundReply(String message) {
        reply = new Reply(NOT_FOUND_REPLY, message);
    }
}
