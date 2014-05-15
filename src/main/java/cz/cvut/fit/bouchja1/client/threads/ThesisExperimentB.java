/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.threads;

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
 * Účelem experimentu bylo zjistit, jak se vyvine situace, kdy má algoritmus
 * výrazný úspěch a po čase ho začnu srážet dolu. K této situaci uměle dojde po
 * obdržení 25. žádosti o doporučení na systém. Dle předpokladu by měl tento
 * algoritmus (využil jsem algoritmu toprate) nejprve výrazně převážit nad
 * ostatními, po změně preferencí by měl klesat a šance by měly být rovnoměrně
 * děleny mezi všechny algoritmy.
 */
public class ThesisExperimentB extends Thread {

    private int id;
    private Communication communication;

    public ThesisExperimentB(int name, Communication communication) {
        this.id = name;
        this.communication = communication;
    }

    public void run() {
        for (int counter = 0; counter < 50; counter++) {
            System.out.println("");
            System.out.println("**** Vypis seznam vsech kontextovych kolekci ****");
            JSONObject allCollectionsJson = null;
            Response allCollections = communication.getBanditCollections();
            if (allCollections.getStatus() != 200) {
                System.out.println("Status: " + allCollections.getStatus());
                allCollectionsJson = new JSONObject(allCollections.readEntity(String.class));
                System.out.println(allCollectionsJson.toString());            
            } else {
                System.out.println("---- vsechny kontextove kolekce ----");
                allCollectionsJson = new JSONObject(allCollections.readEntity(String.class));
                System.out.println(allCollectionsJson.toString());
            }

            System.out.println("");
            System.out.println("**** Vypis seznam vsech kontextovych SUPER kolekci ****");
            JSONObject allSuperCollectionsJson = null;
            Response allSuperCollections = communication.getBanditSuperCollections();
            if (allSuperCollections.getStatus() != 200) {
                System.out.println("Chyba pri vypisu kolekci banditu: " + allSuperCollections.getStatus());
                allSuperCollectionsJson = new JSONObject(allSuperCollections.readEntity(String.class));
                System.out.println(allSuperCollectionsJson.toString());
            } else {
                System.out.println("---- vsechny kontextove SUPER kolekce ----");
                allSuperCollectionsJson = new JSONObject(allSuperCollections.readEntity(String.class));
                System.out.println(allSuperCollectionsJson.toString());
            }

            //Pro ucely experimentu vybiram vzdy z jedne kolekce (pokud existuje)
            int firstContextCollection = 0;
            if (allCollectionsJson != null) {
                JSONArray contextCollectionsArray = allCollectionsJson.getJSONArray("collections");
                firstContextCollection = contextCollectionsArray.getInt(0);
            }

            if (firstContextCollection != 0) {
                System.out.println("");
                System.out.println("**** Vypis nejlepsiho bandity v dane kolekci ****");
                JSONObject bestBanditCollectionJson = null;
                Response bestColl = communication.getBestBanditContextCollectionFilter(firstContextCollection + "", "best");
                if (bestColl.getStatus() != 200) {
                    System.out.println("Chyba pri vypisu kolekci banditu: " + bestColl.getStatus());
                    bestBanditCollectionJson = new JSONObject(bestColl.readEntity(String.class));
                    System.out.println(bestBanditCollectionJson.toString());            
                } else {
                    System.out.println("---- vyber nejlepsiho bandity z kontextove kolekce ----");
                    bestBanditCollectionJson = new JSONObject(bestColl.readEntity(String.class));
                    System.out.println(bestBanditCollectionJson.toString());
                }
                //Uzivatel si zvoli toho doporucovaneho banditu, zasle feedback o volbe    
                if (bestBanditCollectionJson != null) {
                    int bestBandit = bestBanditCollectionJson.getInt("bestBandit");
                    String banditId = bestBanditCollectionJson.getString("banditId");
                    int collectionId = bestBanditCollectionJson.getInt("collection");

                    Response useEnsembleResponse = communication.sendUseEnsembleOperationCollection(collectionId, bestBandit);
                    if (useEnsembleResponse.getStatus() != 200) {
                        System.out.println("Chyba pri zasilani zpetne vazby o pouziti algoritmu.");
                    JSONObject jo = new JSONObject(useEnsembleResponse.readEntity(String.class));
                    System.out.println(jo.toString());                        
                    } else {
                        bestBanditCollectionJson = new JSONObject(useEnsembleResponse.readEntity(String.class));
                        System.out.println(bestBanditCollectionJson.toString());
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
                        case "cfitem" :
                        case "toprate":
                            coreIdToUse = "behavioralCore";
                            break;
                    }

                    parameters.put("coreId", coreIdToUse);

                    Response algorithmRecommendation = communication.getRecommendationByAlgorithm(algorithmToUse, parameters);

                    if (algorithmRecommendation.getStatus() != 200) {
                        System.out.println("Nastal nejaky problem pri doporucovani danym algoritmem: " + algorithmRecommendation.getStatus());
                    JSONObject jo = new JSONObject(algorithmRecommendation.readEntity(String.class));
                    System.out.println(jo.toString());                           
                    } else {
                        //bestBanditCollectionJson = new JSONObject(algorithmRecommendation.readEntity(String.class));
                        //System.out.println(bestBanditCollectionJson.toString());                    
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
                        System.out.println("K hodnoceni si vybiram dokument s ID: " + docId);

                        //nyni mam vybrany nahodny dokument, tak jej budu hodnotit
                        //zaslani zpetne vazby uzivatelova hodnoceni
                        //zaslani zpetne vazby pro ensemble
                        int rating = r.nextInt(5) + 1; //between 1 and 5
                        Response userRatingFeedbackResp = communication.sendUserRatingItemFeedback(docId, "behavioralCore", id, rating);

                        if (userRatingFeedbackResp.getStatus() != 200) {
                            System.out.println("Stala se chyba pri zasilani zpeten vazby hodncoeni clanku: " + userRatingFeedbackResp.getStatus());
                    JSONObject jo = new JSONObject(userRatingFeedbackResp.readEntity(String.class));
                    System.out.println(jo.toString());                               
                        } else {
                            //bestBanditCollectionJson = new JSONObject(userRatingFeedbackResp.readEntity(String.class));
                            //System.out.println(bestBanditCollectionJson.toString());

                            //pokud to bude ten a ten algoritmus, vzdy pozitivni vazba
                            //ostatni s pravdepodobnosti 30 procent
                            String feedback = "";
                            if (bestBandit == 3) {
                                System.out.println("COUNTER: " + counter);
                                if (counter < 25) {
                                    feedback = "possitive";
                                } else {
                                    feedback = "negative";
                                }
                            } else {
                                int randomChance = r.nextInt(100) + 1;
                                if (randomChance < 30) {
                                    feedback = "possitive";
                                } else {
                                    feedback = "negative";
                                }
                            }

                            Response userEnsembleFeedback = communication.sendFeedbackEnsembleOperation(collectionId, bestBandit, feedback);

                            if (userEnsembleFeedback.getStatus() != 200) {
                                System.out.println("Nastala chyba pri zasilani zpetne vazby na ensemble");
                    JSONObject jo = new JSONObject(userEnsembleFeedback.readEntity(String.class));
                    System.out.println(jo.toString());                                   
                            } else {
                                bestBanditCollectionJson = new JSONObject(userEnsembleFeedback.readEntity(String.class));
                                System.out.println(bestBanditCollectionJson.toString());
                            }
                        }

                    }
                }
            }

            System.out.println("");
            System.out.println("**** Vypis nejlepsiho banditu v dane SUPER kolekci ****");
            JSONObject bestBanditSuperCollectionJson = null;
            Response bestSuperColl = communication.getBestBanditSuperCollectionFilter("1", "best");
            if (bestSuperColl.getStatus() != 200) {
                System.out.println("Chyba pri vypisu kolekci SUPER banditu: " + bestSuperColl.getStatus());
                bestBanditSuperCollectionJson = new JSONObject(bestSuperColl.readEntity(String.class));
                System.out.println(bestBanditSuperCollectionJson.toString());
            } else {
                System.out.println("---- vyber nejlepsiho bandity ze SUPER kontextove kolekce ----");
                bestBanditSuperCollectionJson = new JSONObject(bestSuperColl.readEntity(String.class));
                System.out.println(bestBanditSuperCollectionJson.toString());
            }
        }
    }
}
