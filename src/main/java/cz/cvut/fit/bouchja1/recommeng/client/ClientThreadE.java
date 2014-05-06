/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.recommeng.client;

import cz.cvut.fit.bouchja1.client.api.Communication;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jan
 */
public class ClientThreadE extends Thread {

    private String name;
    private Communication communication;

    public ClientThreadE(String name, Communication communication) {
        this.name = name;
        this.communication = communication;
    }

    public void run() {
        System.out.println("Client " + name + " - communication started");
        
        /*
        // vypise si seznam dostupnych kolekci a superkolekci          
        JSONObject allCollections = communication.getBanditCollections();
        System.out.println(allCollections.toString());
        
        JSONObject allSupercollections = communication.getBanditSuperCollections();
        System.out.println(allSupercollections.toString());
       
        
        //dotaze se ensemble na nejlepsi volbu, kterou nabizi zadana kolekce a superkolekce        
        String collectionId = "1";
        JSONObject bestChoiceFromContextCollection = communication.getBestBanditContextCollection(collectionId);
        System.out.println(bestChoiceFromContextCollection.toString());
        
        String supercollectionId = "2";
        JSONObject bestChoiceFromSupercollection = communication.getBestBanditSuperCollection(supercollectionId);
        System.out.println(bestChoiceFromSupercollection.toString());
 
        JsonObject collection = Json.createObjectBuilder()
                .add("name", "morning")
                .add("banditIds", Json.createArrayBuilder()
                .add("latest")
                .add("random"))
                .build();
        
        JSONObject createContextCollectionResp = communication.createContextCollectionRest(collection);
        System.out.println(createContextCollectionResp.toString());
        
        JsonObject collection2 = Json.createObjectBuilder()
                .add("name", "Europe")
                .add("banditIds", Json.createArrayBuilder()
                .add("latest")
                .add("random"))
                .build();
        
        JSONObject createContextCollectionResp2 = communication.createContextCollectionRest(collection2);
        System.out.println(createContextCollectionResp2.toString());  
        
         //pred vytvorenim superkolekce si chce zkontrolovat, jake kolekce existuji v systemu
        
        JSONObject allCollections2 = communication.getBanditCollections();
        System.out.println(allCollections2.toString());        
                       
        JsonObjectBuilder supercollectionBuilder = Json.createObjectBuilder()
                .add("name", "morning in Europe");
        JsonArrayBuilder contextCollectionsBuilder = Json.createArrayBuilder();
        
        JSONArray a = allCollections2.getJSONArray("collections");
        for (int i = 0; i < a.length(); i++) {
            contextCollectionsBuilder.add((int)a.get(i));
        }        
        supercollectionBuilder.add("contextCollections", contextCollectionsBuilder);
        JsonObject supercollection = supercollectionBuilder.build();                
        JSONObject createSupercollectionResp = communication.createBanditSuperCollectionRest(supercollection);
        System.out.println(createSupercollectionResp.toString());

        */
         //dotaz na nejlepsi volbu z kolekce
        
        //zadost na kolekci
        String collectionId2 = "1";
        String filter = "best";
        JSONObject bestChoiceFromContextCollection2 = communication.getBestBanditContextCollectionFilter(collectionId2, filter);
        System.out.println(bestChoiceFromContextCollection2.toString());         
        
        //Bude se ridit doporucenim ensemble, zvoli co mu doporucil z kolekce
        int contextCollection = bestChoiceFromContextCollection2.getInt("collection");
        int bestBandit = bestChoiceFromContextCollection2.getInt("bestBandit");
        JSONObject bestChoiceFromContextCollection3 = communication.sendUseEnsembleOperationCollection(contextCollection, bestBandit);
        System.out.println(bestChoiceFromContextCollection3.toString());
        
        //zadost na superkolekci
        String supercollectionId2 = "1";
        String filter2 = "best";
        JSONObject bestChoiceFromSuperCollection2 = communication.getBestBanditSuperCollectionFilter(supercollectionId2, filter2);
        System.out.println(bestChoiceFromSuperCollection2.toString());  
        
        //Bude se ridit doporucenim ensemble, zvoli co mu doporucil ze superkolekce
        int contextSuperCollection = bestChoiceFromSuperCollection2.getInt("collection");
        int bestBanditSuper = bestChoiceFromSuperCollection2.getInt("bestBandit");
        JSONObject bestChoiceFromContextCollection4 = communication.sendUseEnsembleOperationSupercollection(contextSuperCollection, bestBanditSuper);
        System.out.println(bestChoiceFromContextCollection4.toString());        
    }
}
