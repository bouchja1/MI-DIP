/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.api;

import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 *
 * @author jan
 */
public class Communication {

    private EnsembleClientApi clientApi;

    public Communication(EnsembleClientApi clientApi) {
        this.clientApi = clientApi;
    }

    public void getRandomRecommendation() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/articleCore/random")
                .queryParam("groupId", 123)
                .queryParam("limit", 12)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    public void getLatestRecommendation() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/articleCore/latest")
                .queryParam("groupId", 123)
                .queryParam("limit", 12)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    public void getTopratedRecommendation() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/behavioralCore/toprate")
                .queryParam("groupId", 0)
                .queryParam("limit", 12)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    public void sendUserRatingItemFeedback() {
        JsonObject collection = Json.createObjectBuilder()
                .add("articleId", "oavgofrsdxxcos5x1man")
                .add("userId", 2)
                .add("rating", 4.0)
                .build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/core/behavioralCore/document")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .post(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    /*
     *************************************** ENSEMBLE
     */
    public void createContextCollectionRest() {
        JsonObject collection = Json.createObjectBuilder()
                .add("name", "vecer")
                .add("banditIds", Json.createArrayBuilder()
                .add("latest")
                .add("random"))
                .build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/collection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .post(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    public void createBanditSuperCollection() {

        JsonObjectBuilder supercollectionBuilder = Json.createObjectBuilder()
                .add("name", "rano v Evrope");
        JsonArrayBuilder contextCollectionsBuilder = Json.createArrayBuilder();
        contextCollectionsBuilder.add(1);
        contextCollectionsBuilder.add(3);
        supercollectionBuilder.add("contextCollections", contextCollectionsBuilder);
        JsonObject supercollection = supercollectionBuilder.build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/supercollection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .post(Entity.entity(supercollection, MediaType.APPLICATION_JSON_TYPE));

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    public void getBanditCollections() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/collection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        System.out.println(response.getStatus());

        final JSONObject jsonObj = new JSONObject(response.readEntity(String.class));
        /*
         if (jsonObj != null && jsonObj.isNull("location")) {
         final JSONObject location = (JSONObject) jsonObj.get("location");
         }
         */
        System.out.println(jsonObj.toString());
    }

    public void getBanditSuperCollections() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/supercollection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        System.out.println(response.getStatus());

        final JSONObject jsonObj = new JSONObject(response.readEntity(String.class));
        /*
         if (jsonObj != null && jsonObj.isNull("location")) {
         final JSONObject location = (JSONObject) jsonObj.get("location");
         }
         */
        System.out.println(jsonObj.toString());
    }

    public void getBestBanditCollection() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/collection/1")
                //.queryParam("filter", "best")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        System.out.println(response.getStatus());

        final JSONObject jsonObj = new JSONObject(response.readEntity(String.class));
        System.out.println(jsonObj.toString());
    }

    public void getBestBanditSuperCollection() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/ensemble/supercollection/1")
                .queryParam("filter", "best")
                //.queryParam("filter", "best")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        System.out.println(response.getStatus());

        final JSONObject jsonObj = new JSONObject(response.readEntity(String.class));
        System.out.println(jsonObj.toString());
    }

    public void sendUseEnsembleOperation() {
        JsonObject collection = Json.createObjectBuilder()
                .add("bandit", "random")
                .add("operation", "use")
                .build();

        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                //.path("recommeng/ensemble/collection/1")
                .path("recommeng/ensemble/supercollection/1")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .put(Entity.entity(collection, MediaType.APPLICATION_JSON_TYPE));

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));

    }

    public void sendFeedbackEnsembleOperation() {
        JsonObject collection = Json.createObjectBuilder()
                .add("bandit", "random")
                .add("operation", "feedback")
                //.add("feedbackType", "possitive")
                .add("feedbackType", "negative")
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

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    public void sendSuperFeedbackEnsembleOperation() {
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

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    public void deleteDocumentInCore() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/core/articleCore/document")
                .queryParam("documentId", "rwpuxn3yo0 ivjlyg 5h")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .delete();

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    public void getCfUserRecommendation() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/behavioralCore/cfuser")
                .queryParam("groupId", 0)
                .queryParam("limit", 12)
                .queryParam("userId", 2)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    public void getCfItemRecommendation() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/behavioralCore/cfitem")
                .queryParam("groupId", 0)
                .queryParam("limit", 12)
                .queryParam("userId", 2)
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    public void getMltRecommendation() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/articleCore/mlt")
                .queryParam("groupId", 0)
                .queryParam("limit", 12)
                .queryParam("documentId", "http://pnjj5cr4f9 f500k9vld.org")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }    
    
    public void createArticlesInCore(List<JsonObject> collectionOfArticles) {
        Client client = clientApi.getClient();
        for (JsonObject o : collectionOfArticles) {
            Response response = client.target(clientApi.getRestfulApiLocation())
                    .path("recommeng/core/articleCore/article")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    //.header("some-header", "true")
                    .post(Entity.entity(o, MediaType.APPLICATION_JSON_TYPE));

            System.out.println(response.getStatus());
            System.out.println(response.readEntity(String.class));
        }
    }
}
