/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.recommeng.client;

import cz.cvut.fit.bouchja1.client.api.EnsembleClientApi;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 *
 * @author jan
 */
public class ClientThread extends Thread {

    private String name;
    private EnsembleClientApi clientApi;

    public ClientThread(String name, EnsembleClientApi clientApi) {
        this.name = name;
        this.clientApi = clientApi;
    }

    public void run() {


        //ALGORITHMS
        //getRandomRecommendation();

        //ENSEMBLE
        //createContextCollectionRest();
        //createBanditSuperCollection();
        //getBanditCollections();
        //getBanditSuperCollections();
        
        //getBestBanditCollection();
        //getBestBanditSuperCollection();
        
        //sendUseEnsembleOperation();        
        //sendFeedbackEnsembleOperation();
        sendSuperFeedbackEnsembleOperation();
    }

    private void getRandomRecommendation() {
        Client client = clientApi.getClient();

        Response response = client.target(clientApi.getRestfulApiLocation())
                .path("recommeng/algorithm/articleCore/random")
                .queryParam("groupId", "123")
                .queryParam("limit", "12")
                .request(MediaType.APPLICATION_JSON_TYPE)
                //.header("some-header", "true")
                .get();

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    /*
     *************************************** ENSEMBLE
     */
    private void createContextCollectionRest() {
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
}
