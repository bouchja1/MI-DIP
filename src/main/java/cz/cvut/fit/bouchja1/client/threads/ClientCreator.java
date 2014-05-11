/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.threads;

import cz.cvut.fit.bouchja1.client.api.Communication;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jan
 */
public class ClientCreator extends Thread {

    private int id;
    private Communication communication;

    public ClientCreator(int name, Communication communication) {
        this.id = name;
        this.communication = communication;
    }

    public void run() {
        System.out.println("Client creator with " + id + " - communication started");    
        
        System.out.println("**** Client creator - zjisteni kontextovych kolekci ****");
        JSONObject allCollectionsJson = null;
        Response allCollections = communication.getBanditCollections();
        if (allCollections.getStatus() != 200) {
            System.out.println("Client creator - stala se chyba pri vraceni seznamu koleci: " + allCollections.getStatus());
        } else {
            /*
            allCollectionsJson = new JSONObject(allCollections.readEntity(String.class));
            if (!allCollectionsJson.isNull("collections")) {
                JSONArray collectionsArray = allCollectionsJson.getJSONArray("collections");
                
            } else {
                //vytvor kolekce
                createMorningAndEurope();
            }
            */
            List<Integer> createdCollections = createMorningAndEurope();
            if (!createdCollections.isEmpty()) {
                createSuperCollectionMorningInEurope(createdCollections);
            }
        }                       
    }

    private List<Integer> createMorningAndEurope() {
        List<Integer> createdCollections = new ArrayList<>();
        //vytvoreni kontextovych kolekci
        JsonObject collection = Json.createObjectBuilder()
        .add("name", "morning")
        .add("banditIds", Json.createArrayBuilder()
        .add("latest")
        .add("cfuser")
        .add("toprate")
        .add("random"))
        .build();
        Response createContextCollectionResp = communication.createContextCollectionRest(collection);
        JSONObject createContextCollectionRespJson = new JSONObject(createContextCollectionResp.readEntity(String.class));
        System.out.println(createContextCollectionRespJson.toString());
        if (createContextCollectionResp.getStatus() == 200) {
            createdCollections.add(createContextCollectionRespJson.getInt("collection"));
        }
        
        JsonObject collection2 = Json.createObjectBuilder()
                .add("name", "Europe")
                .add("banditIds", Json.createArrayBuilder()
                .add("latest")
                .add("cfuser")
                .add("toprate")
                .add("random"))
                .build();
        
        Response createContextCollectionResp2 = communication.createContextCollectionRest(collection2);
        JSONObject createContextCollectionResp2Json = new JSONObject(createContextCollectionResp2.readEntity(String.class));
        System.out.println(createContextCollectionResp2Json.toString()); 
        if (createContextCollectionResp2.getStatus() == 200) {
            createdCollections.add(createContextCollectionResp2Json.getInt("collection"));
        }
        return createdCollections;
    }

    private void createSuperCollectionMorningInEurope(List<Integer> createdCollections) {
        JsonArrayBuilder contextCollections = Json.createArrayBuilder();
        for (Integer collId : createdCollections) {
            contextCollections.add(collId);
        }
        
        JsonObject superCollection = Json.createObjectBuilder()
                .add("name", "Morning in Europe")
                .add("contextCollections", contextCollections)
                .build();        
        
        Response createSuperCollectionResp = communication.createBanditSuperCollectionRest(superCollection);
        JSONObject createSuperCollectionRespJson = new JSONObject(createSuperCollectionResp.readEntity(String.class));
        System.out.println(createSuperCollectionRespJson.toString());       
    }
}
