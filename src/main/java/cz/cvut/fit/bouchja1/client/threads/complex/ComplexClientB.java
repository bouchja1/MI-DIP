/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.threads.complex;

import cz.cvut.fit.bouchja1.client.api.Communication;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jan
 *
 */
public class ComplexClientB extends Thread {

    private int id;
    private Communication communication;
    
        private final String NAME;

    public ComplexClientB(int name, Communication communication) {
        this.id = name;
        this.communication = communication;
                this.NAME = "Client " + id;
    }

    public void run() {
        //System.out.println("");
        //System.out.println("**** Vypis seznam vsech kontextovych kolekci ****");
        JSONObject allCollectionsJson = null;
        Response allCollections = communication.getBanditCollections();
        if (allCollections.getStatus() != 200) {
            //System.out.println("Status: " + allCollections.getStatus());
            allCollectionsJson = new JSONObject(allCollections.readEntity(String.class));
            //System.out.println(allCollectionsJson.toString());            
        } else {
            //System.out.println("---- vsechny kontextove kolekce ----");
            allCollectionsJson = new JSONObject(allCollections.readEntity(String.class));
            //System.out.println(allCollectionsJson.toString());
        }

        //System.out.println("");
        //System.out.println("**** Vypis seznam vsech kontextovych SUPER kolekci ****");
        JSONObject allSuperCollectionsJson = null;
        Response allSuperCollections = communication.getBanditSuperCollections();
        if (allSuperCollections.getStatus() != 200) {
            //System.out.println("Chyba pri vypisu kolekci banditu: " + allSuperCollections.getStatus());
            allSuperCollectionsJson = new JSONObject(allCollections.readEntity(String.class));
            //System.out.println(allSuperCollectionsJson.toString());
        } else {
            //System.out.println("---- vsechny kontextove SUPER kolekce ----");
            allSuperCollectionsJson = new JSONObject(allSuperCollections.readEntity(String.class));
            //System.out.println(allSuperCollectionsJson.toString());
        }

        //Pro ucely experimentu vybiram vzdy z jedne kolekce (pokud existuje)
        int firstContextCollection = 0;
        if (allSuperCollectionsJson != null) {
            JSONArray contextCollectionsArray = allCollectionsJson.getJSONArray("collections");
            firstContextCollection = contextCollectionsArray.getInt(0);
        }

        if (firstContextCollection != 0) {
            //System.out.println("");
            //System.out.println("**** Vypis nejlepsiho bandity v dane kolekci ****");
            
        JSONObject bestBanditSuperCollectionJson = null;
        Response bestSuperColl = communication.getBestBanditSuperCollectionFilter(firstContextCollection+"", "best");
        if (bestSuperColl.getStatus() != 200) {
            //System.out.println("Chyba pri vypisu kolekci SUPER banditu: " + bestSuperColl.getStatus());
            //bestBanditSuperCollectionJson = new JSONObject(bestSuperColl.readEntity(String.class));
            //System.out.println(bestBanditSuperCollectionJson.toString());
        } else {
            //System.out.println("---- vyber nejlepsiho bandity ze SUPER kontextove kolekce ----");
            bestBanditSuperCollectionJson = new JSONObject(bestSuperColl.readEntity(String.class));
            //System.out.println(bestBanditSuperCollectionJson.toString());
        }            
                                   
            //Uzivatel si zvoli toho doporucovaneho banditu, zasle feedback o volbe    
            if (bestBanditSuperCollectionJson != null) {
                int bestBandit = bestBanditSuperCollectionJson.getInt("bestBandit");
                String banditId = bestBanditSuperCollectionJson.getString("banditId");
                int collectionId = bestBanditSuperCollectionJson.getInt("collection");

                System.out.println(NAME + " is choosing bandit: " + bestBandit + " (" + banditId + ")");
                
                Response useEnsembleResponse = communication.sendUseEnsembleOperationSupercollection(collectionId, bestBandit);
                if (useEnsembleResponse.getStatus() != 200) {
                    //System.out.println("Chyba pri zasilani zpetne vazby o pouziti algoritmu.");
                } else {
                    bestBanditSuperCollectionJson = new JSONObject(useEnsembleResponse.readEntity(String.class));
                    //System.out.println(bestBanditSuperCollectionJson.toString());
                }

                //nechavame si doporucit konkretnim algoritmem
                String algorithmToUse = banditId;
                Map<String, String> parameters = new HashMap<>();
                parameters.put("limit", "15");
                parameters.put("userId", id + "");

                String coreIdToUse = "";
                switch (algorithmToUse) {
                    case "latest":
                    case "random":
                    case "mlt":
                        coreIdToUse = "articleCore";
                        break;
                    case "cfuser":
                        case "cfitem":
                    case "toprate":
                        coreIdToUse = "behavioralCore";
                        break;
                }

                parameters.put("coreId", coreIdToUse);

                Response algorithmRecommendation = communication.getRecommendationByAlgorithm(algorithmToUse, parameters);

                if (algorithmRecommendation.getStatus() != 200) {
                    //System.out.println("Nastal nejaky problem pri doporucovani danym algoritmem: " + algorithmRecommendation.getStatus());
                } else {
                    //bestBanditSuperCollectionJson = new JSONObject(algorithmRecommendation.readEntity(String.class));
                    //System.out.println(bestBanditSuperCollectionJson.toString());                    
                    JSONObject mainObj = new JSONObject();
                    JSONArray ja = new JSONArray(algorithmRecommendation.readEntity(String.class));
                    mainObj.put("articles", ja);

                    JSONArray articles = mainObj.getJSONArray("articles");

                    //vyberu si nahodne nektery z navracenych clanku a ohodnotim jej nahodne od 1 do 5
                    //tim jak hodnotil se zaroven posle budto kladna ci zaporna vazba na ensemble    

                    Random r = new Random();
                    int randomDocIndex = r.nextInt(articles.length());
                    JSONObject obj = (JSONObject) articles.get(randomDocIndex);
                    String docId = obj.getString("documentId");
                    //System.out.println("K hodnoceni si vybiram dokument s ID: " + docId);

                    //nyni mam vybrany nahodny dokument, tak jej budu hodnotit
                    //zaslani zpetne vazby uzivatelova hodnoceni
                    //zaslani zpetne vazby pro ensemble
                    int rating = r.nextInt(5) + 1; //between 1 and 5
                    Response userRatingFeedbackResp = communication.sendUserRatingItemFeedback(docId, "behavioralCore", id, rating);

                    System.out.println(NAME + "'s rating: " + rating);
                    
                    if (userRatingFeedbackResp.getStatus() != 200) {
                        //System.out.println("Stala se chyba pri zasilani zpeten vazby hodncoeni clanku: " + userRatingFeedbackResp.getStatus());
                    } else {
                        //bestBanditSuperCollectionJson = new JSONObject(userRatingFeedbackResp.readEntity(String.class));
                        //System.out.println(bestBanditSuperCollectionJson.toString());
                        String feedback = "";

                        if (rating > 2) {
                            feedback = "possitive";
                        } else {
                            feedback = "negative";
                        }
                        
                        System.out.println(NAME + "'s feedback: " + feedback);
                        
                        Response userEnsembleFeedback = communication.sendSuperFeedbackEnsembleOperation(collectionId, bestBandit, feedback);

                        if (userEnsembleFeedback.getStatus() != 200) {
                            //System.out.println("Nastala chyba pri zasilani zpetne vazby na ensemble");
                        } else {
                            bestBanditSuperCollectionJson = new JSONObject(userEnsembleFeedback.readEntity(String.class));
                            //System.out.println(bestBanditSuperCollectionJson.toString());
                        }  
                    }

                }
            }
            
            
        }
    }
}
