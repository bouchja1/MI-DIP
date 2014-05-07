/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.api;

import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jan
 */
public class Communication {

    protected EnsembleClient clientApi;

    public Communication(EnsembleClient clientApi) {
        this.clientApi = clientApi;
    }
    
    public JSONObject getRandomRecommendation(Map<String, String> parameters) {
        Client client = clientApi.getClient();

        int groupId = 0;
        int limit = 0;
        if (parameters.get("groupId") != null) {
            groupId = Integer.parseInt(parameters.get("groupId"));
        }
        if (parameters.get("limit") != null) {
            limit = Integer.parseInt(parameters.get("limit"));
        }
        
        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/articleCore/random")
                .queryParam("groupId", groupId)
                .queryParam("limit", limit)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();
        JSONArray ja = new JSONArray(response.readEntity(String.class));
        JSONObject mainObj = new JSONObject();
        mainObj.put("articles", ja);        
        
        return mainObj;
    }

    public JSONObject getLatestRecommendation(Map<String, String> parameters) {
        Client client = clientApi.getClient();
        
        int groupId = 0;
        int limit = 0;
        if (parameters.get("groupId") != null) {
            groupId = Integer.parseInt(parameters.get("groupId"));
        }
        if (parameters.get("limit") != null) {
            limit = Integer.parseInt(parameters.get("limit"));
        }        

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/articleCore/latest")
                .queryParam("groupId", groupId)
                .queryParam("limit", limit)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();
        //JSONObject jsonObj = new JSONObject(response.readEntity(String.class));
        JSONArray ja = new JSONArray(response.readEntity(String.class));
        JSONObject mainObj = new JSONObject();
        mainObj.put("articles", ja);        
        
        return mainObj;
    }

    public JSONObject getTopratedRecommendation(Map<String, String> parameters) {
        Client client = clientApi.getClient();

        int groupId = 0;
        int limit = 0;
        if (parameters.get("groupId") != null) {
            groupId = Integer.parseInt(parameters.get("groupId"));
        }
        if (parameters.get("limit") != null) {
            limit = Integer.parseInt(parameters.get("limit"));
        }        
        
        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/behavioralCore/toprate")
                .queryParam("groupId", groupId)
                .queryParam("limit", limit)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();
        JSONArray ja = new JSONArray(response.readEntity(String.class));
        JSONObject mainObj = new JSONObject();
        mainObj.put("articles", ja);        
        
        return mainObj;
    }  
    
    public JSONObject getCfUserRecommendation(Map<String, String> parameters) {
        Client client = clientApi.getClient();

        int groupId = 0;
        int limit = 0;
        int userId = 0;
        if (parameters.get("groupId") != null) {
            groupId = Integer.parseInt(parameters.get("groupId"));
        }
        if (parameters.get("limit") != null) {
            limit = Integer.parseInt(parameters.get("limit"));
        }        
        if (parameters.get("userId") != null) {
            userId = Integer.parseInt(parameters.get("userId"));
        }                
        
        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/behavioralCore/cfuser")
                .queryParam("groupId", groupId)
                .queryParam("limit", limit)
                .queryParam("userId", userId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();
        JSONArray ja = new JSONArray(response.readEntity(String.class));
        JSONObject mainObj = new JSONObject();
        mainObj.put("articles", ja);        
        
        return mainObj;
    }

    public JSONObject getCfItemRecommendation(Map<String, String> parameters) {
        Client client = clientApi.getClient();

        int groupId = 0;
        int limit = 0;
        int userId = 0;
        if (parameters.get("groupId") != null) {
            groupId = Integer.parseInt(parameters.get("groupId"));
        }
        if (parameters.get("limit") != null) {
            limit = Integer.parseInt(parameters.get("limit"));
        }        
        if (parameters.get("userId") != null) {
            userId = Integer.parseInt(parameters.get("userId"));
        }           
        
        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/behavioralCore/cfitem")
                .queryParam("groupId", groupId)
                .queryParam("limit", limit)
                .queryParam("userId", userId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();
        JSONArray ja = new JSONArray(response.readEntity(String.class));
        JSONObject mainObj = new JSONObject();
        mainObj.put("articles", ja);        
        
        return mainObj;
    }

    public JSONObject getMltRecommendation(Map<String, String> parameters) {
        Client client = clientApi.getClient();

        int groupId = 0;
        int limit = 0;
        String documentId = null;
        if (parameters.get("groupId") != null) {
            groupId = Integer.parseInt(parameters.get("groupId"));
        }
        if (parameters.get("limit") != null) {
            limit = Integer.parseInt(parameters.get("limit"));
        }        
        if (documentId != null) {
            documentId = parameters.get("documentId");
        }           
        
        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/articleCore/mlt")
                .queryParam("groupId", groupId)
                .queryParam("limit", limit)
                .queryParam("documentId", documentId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();
        JSONArray ja = new JSONArray(response.readEntity(String.class));
        JSONObject mainObj = new JSONObject();
        mainObj.put("articles", ja);        
        
        return mainObj;
    }  
    
    public Response sendUserRatingItemFeedback(String articleId, int userId, int rating) {
        double doubleRat = (double) rating;
        JsonObject collection = Json.createObjectBuilder()
                .add("articleId", articleId)
                .add("userId", userId)
                .add("rating", doubleRat)
                .build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/articles/behavioralCore/document")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .post(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));
        return response;
    }

    public JSONObject deleteDocumentInCore(String documentId) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/articles/articleCore/document")
                .queryParam("documentId", documentId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .delete();
        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;
    }  
    
    public void createArticlesInCore(List<JsonObject> collectionOfArticles) {
        Client client = clientApi.getClient();
        for (JsonObject o : collectionOfArticles) {
            Response response = client.target(clientApi.getRestfulApiLocation())
                    .path("recommeng/articles/articleCore/article")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    //.header("some-header", "true")
                    .post(Entity.entity(o, MediaType.APPLICATION_JSON_TYPE));

            System.out.println(response.getStatus());
            System.out.println(response.readEntity(String.class));
        }
    }   
    
    /*
     *************************************** ENSEMBLE
     */
    public JSONObject createContextCollectionRest(JsonObject collection) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/collection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .post(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));

        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;
    }

    public JSONObject createBanditSuperCollectionRest(JsonObject supercollection) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/supercollection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .post(Entity.entity(supercollection, MediaType.APPLICATION_JSON_TYPE));

        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;
    }

    public JSONObject getBanditCollections() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/collection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));
        return jsonObj;
    }

    public JSONObject getBanditSuperCollections() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/supercollection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        System.out.println(response.getStatus());

        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;
    }

    public JSONObject getBestBanditContextCollectionFilter(String collectionId, String filter) {        
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/collection/" + collectionId)
                .queryParam("filter", filter)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        System.out.println(response.getStatus());

        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;
    }    
    
    public JSONObject getBestBanditSuperCollectionFilter(String collectionId, String filter) {        
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/supercollection/" + collectionId)
                .queryParam("filter", filter)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        System.out.println(response.getStatus());

        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;
    }        
    
    public JSONObject getBestBanditContextCollection(String collectionId) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/collection/" + collectionId)
                //.queryParam("filter", "best")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        System.out.println(response.getStatus());

        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;
    }

    public JSONObject getBestBanditSuperCollection(String supercollectionId) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/supercollection/" + supercollectionId)
                .queryParam("filter", "best")
                //.queryParam("filter", "best")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        System.out.println(response.getStatus());

        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;
    }
    
    public JSONObject sendUseEnsembleOperationCollection(int contextCollection, int bandit) {
        JsonObject collection = Json.createObjectBuilder()
                .add("bandit", bandit)
                .add("operation", "use")
                .build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                //.path("recommeng/ensemble/collection/1")
                .path("recommeng/ensemble/collection/" + contextCollection)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));

        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;

    }    

    public JSONObject sendUseEnsembleOperationSupercollection(int contextCollection, int bandit) {
        JsonObject collection = Json.createObjectBuilder()
                .add("bandit", bandit)
                .add("operation", "use")
                .build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                //.path("recommeng/ensemble/collection/1")
                .path("recommeng/ensemble/supercollection/" + contextCollection)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));

        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;

    }

    public JSONObject sendFeedbackEnsembleOperation(int banditId, String feedbackType) {
        JsonObject collection = Json.createObjectBuilder()
                .add("bandit", banditId)
                .add("operation", "feedback")
                //.add("feedbackType", "possitive")
                .add("feedbackType", feedbackType)
                .build();

        /*
         * TODO testovat na to, co se stane kdyz se tam hodi blba kolekce, kdyz se tam hodi blbej nazev operace a tak
         JsonObject collection = Json.createObjectBuilder()
         .add("bandit", "random")
         .add("operation", "feedback")
         .add("feedbackType", "negative")
         .build();
         */

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/collection/1")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));
        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;
    }

    public JSONObject sendSuperFeedbackEnsembleOperation() {
        JsonObject collection = Json.createObjectBuilder()
                .add("bandit", "random")
                .add("operation", "feedback")
                //.add("feedbackType", "possitive")
                .add("feedbackType", "possitive")
                .build();

        /*
         * TODO testovat na to, co se stane kdyz se tam hodi blba kolekce, kdyz se tam hodi blbej nazev operace a tak
         JsonObject collection = Json.createObjectBuilder()
         .add("bandit", "random")
         .add("operation", "feedback")
         .add("feedbackType", "negative")
         .build();
         */

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/supercollection/1")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));
        JSONObject jsonObj = new JSONObject(response.readEntity(String.class));

        return jsonObj;
    }      

    public JSONObject getRecommendation(String algorithmToUse, Map<String, String> parameters) {
        switch (algorithmToUse) {
            case "latest" :
                return getLatestRecommendation(parameters);
                /*
            case "mlt" :
                return getMltRecommendation(parameters);                
                */
            case "random" :
                return getRandomRecommendation(parameters);                
            case "cfuser" :
                return getCfUserRecommendation(parameters);                
            /*
            case "cfitem" :
                return getCfItemRecommendation(parameters);                                
                */
            case "toprate" :
                return getTopratedRecommendation(parameters);                
            default : 
                return null;
        }            
    }
}
