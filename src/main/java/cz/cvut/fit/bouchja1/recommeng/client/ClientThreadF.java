/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.recommeng.client;

import cz.cvut.fit.bouchja1.client.api.Communication;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
public class ClientThreadF extends Thread {

    private int id;
    private Communication communication;

    public ClientThreadF(int id, Communication communication) {
        this.id = id;
        this.communication = communication;
    }

    public void run() {
        System.out.println("Client " + id + " - communication started");
        
        
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

        
         //dotaz na nejlepsi volbu z kolekce
        
        //zadost na kolekci
        String collectionId2 = "1";
        String filter = "best";
        JSONObject bestChoiceFromContextCollection2 = communication.getBestBanditContextCollectionFilter(collectionId2, filter);
        System.out.println(bestChoiceFromContextCollection2.toString());         
        
        //Bude se ridit doporucenim ensemble, zvoli co mu doporucil z kolekce
        if (bestChoiceFromContextCollection2.getInt("collection") != 0) {
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
        
        //ted si necha doporucit tim banditou obsah
        String algorithmToUse = bestChoiceFromSuperCollection2.getString("banditId");        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("limit", "15");
        parameters.put("userId", id+"");
        JSONObject algorithmRecommendation = communication.getRecommendation(algorithmToUse, parameters);
        System.out.println(algorithmRecommendation.toString());    
        
        //ted si vybere nejaky ze clanku a ohodnoti jej od 1 do 5
        //tim jak hodnotil se zaroven posle budto kladna ci zaporna vazba na ensemble    
        JSONArray articles = algorithmRecommendation.getJSONArray("articles");
        Random r = new Random();
        int randomDocIndex = r.nextInt(articles.length());
        JSONObject obj = (JSONObject)articles.get(randomDocIndex);
        String docId = obj.getString("documentId");
        System.out.println("DOC: " + docId);
        
        //nyni mam vybrany nahodny dokument, tak jej budu hodnotit
        //zaslani zpetne vazby uzivatelova hodnoceni
        //zaslani zpetne vazby pro ensemble
        int rating = r.nextInt(5) + 1; //between 1 and 5
        Response userRatingFeedbackResp = communication.sendUserRatingItemFeedback(docId, id, rating);
        
        System.out.println(userRatingFeedbackResp.getStatus() + " " + userRatingFeedbackResp.toString());
        
        String feedback;
        /*
        if (rating > 2) {
            feedback = "possitive";
        } else {
            feedback = "negative";
        }
        */
        feedback = "possitive";
        JSONObject userEnsembleFeedback = communication.sendFeedbackEnsembleOperation(bestBanditSuper, feedback);
        System.out.println(userEnsembleFeedback.toString());            
        }
    }
}
