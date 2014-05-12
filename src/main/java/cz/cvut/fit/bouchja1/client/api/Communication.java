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

    public Response getRecommendationByAlgorithm(String algorithm, Map<String, String> parameters) {
        Client client = clientApi.getClient();

        int groupId = 0;
        int userId = 0;
        int limit = 0;
        String coreId = null;
        String documentId = null;
        if (parameters.get("coreId") != null) {
            coreId = parameters.get("coreId");
        }
        if (parameters.get("documentId") != null) {
            documentId = parameters.get("documentId");
        }
        if (parameters.get("groupId") != null) {
            groupId = Integer.parseInt(parameters.get("groupId"));
        }
        if (parameters.get("userId") != null) {
            userId = Integer.parseInt(parameters.get("userId"));
        }
        if (parameters.get("limit") != null) {
            limit = Integer.parseInt(parameters.get("limit"));
        }

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("algorithm/" + algorithm)
                .queryParam("groupId", groupId)
                .queryParam("userId", userId)
                .queryParam("coreId", coreId)
                .queryParam("documentId", documentId)
                .queryParam("limit", limit)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();
        
        return response;
    }

    public Response sendUserRatingItemFeedback(String articleId, String core, int userId, int rating) {
        double doubleRat = (double) rating;
        JsonObject collection = Json.createObjectBuilder()
                .add("articleId", articleId)
                .add("userId", userId)
                .add("rating", doubleRat)
                .build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("cores/" + core + "/documents")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));
        return response;
    }

    public Response deleteDocumentInCore(String documentId, String core) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("cores/" + core + "/document")
                .queryParam("documentId", documentId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .delete();
        

        return response;
    }

    //INICIALIZACNI
    public void createArticlesInCore(List<JsonObject> collectionOfArticles, String articleCore) {
        Client client = clientApi.getClient();
        for (JsonObject article : collectionOfArticles) {
            Response response = client.target(clientApi.getRestfulApiLocation())
                    .path("cores/" + articleCore + "/documents")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    //.header("some-header", "true")
                    .post(Entity.entity(article, MediaType.APPLICATION_JSON_TYPE));

            System.out.println(response.getStatus());
            System.out.println(response.readEntity(String.class));
        }        
    }

    public Response createBehavioralInCore(JSONArray collectionOfBehavioral, String articleCore) {
        Client client = clientApi.getClient();
        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("cores/" + articleCore + "/documents")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collectionOfBehavioral, MediaType.APPLICATION_JSON_TYPE));

        return response;
    }
    //INICIALIZACNI
    
    /*
     *************************************** ENSEMBLE
     */
    public Response createContextCollectionRest(JsonObject collection) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("collection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .post(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));        

        return response;
    }

    public Response createBanditSuperCollectionRest(JsonObject supercollection) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("supercollection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .post(Entity.entity(supercollection, MediaType.APPLICATION_JSON_TYPE));

        return response;
    }

    public Response getBanditCollections() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("collection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        return response;
    }

    public Response getBanditSuperCollections() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("supercollection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();


        return response;
    }

    public Response getAlgorithmsList() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("algorithm")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        System.out.println(response.getStatus());

        /*
        JSONArray ja = new JSONArray(response.readEntity(String.class));
        JSONObject mainObj = new JSONObject();
        mainObj.put("algorithms", ja);

*/
        return response;
    }

    public Response getBestBanditContextCollectionFilter(String collectionId, String filter) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("collection/" + collectionId)
                .queryParam("filter", filter)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        return response;
    }

    public Response getBestBanditSuperCollectionFilter(String collectionId, String filter) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("supercollection/" + collectionId)
                .queryParam("filter", filter)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        return response;
    }

    public Response getBestBanditContextCollection(String collectionId) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("collection/" + collectionId)
                //.queryParam("filter", "best")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        return response;
    }

    public Response getBestBanditSuperCollection(String supercollectionId) {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("supercollection/" + supercollectionId)
                .queryParam("filter", "best")
                //.queryParam("filter", "best")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        return response;
    }

    public Response sendUseEnsembleOperationCollection(int contextCollection, int bandit) {
        JsonObject collection = Json.createObjectBuilder()
                .add("bandit", bandit)
                .add("operation", "use")
                .build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                //.path("recommeng/ensemble/collection/1")
                .path("collection/" + contextCollection)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));

        return response;

    }

    public Response sendUseEnsembleOperationSupercollection(int contextCollection, int bandit) {
        JsonObject collection = Json.createObjectBuilder()
                .add("bandit", bandit)
                .add("operation", "use")
                .build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                //.path("recommeng/ensemble/collection/1")
                .path("supercollection/" + contextCollection)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));

        return response;

    }

    public Response sendFeedbackEnsembleOperation(int collectionId, int banditId, String feedbackType) {
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
                .path("collection/" + collectionId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));

        return response;
    }

    public Response sendSuperFeedbackEnsembleOperation(int collectionId, int banditId, String feedbackType) {
        JsonObject collection = Json.createObjectBuilder()
                .add("bandit", banditId)
                .add("operation", "feedback")
                //.add("feedbackType", "possitive")
                .add("feedbackType", feedbackType)
                .build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("supercollection/" + collectionId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));

        return response;
    }

    public Response getCoresList() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("cores")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        return response;
    }
}
