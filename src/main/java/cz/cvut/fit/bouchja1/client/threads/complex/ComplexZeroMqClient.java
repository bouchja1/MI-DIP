/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.threads.complex;

import cz.cvut.fit.bouchja1.client.api.Communication;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

/**
 *
 * @author jan
 */
public class ComplexZeroMqClient extends Thread {

    private int id;
    private Communication communication;
    private final String NAME;

    public ComplexZeroMqClient(int name, Communication communication) {
        this.id = name;
        this.communication = communication;
        this.NAME = "Client " + id;
    }

    public void run() {
        ZContext ctx = new ZContext();
        System.out.println("");
        //System.out.println("**** Vypis seznam vsech kontextovych kolekci ****");
        JSONObject allCollectionsJson = null;

        ZMQ.Socket client = ctx.createSocket(ZMQ.DEALER);
        client.connect("tcp://localhost:5555");
        String replyString = "";

        JsonObject json = Json.createObjectBuilder()
                .add("method", "GET")
                .add("path", "/ensemble/services/collection")
                .build();
        client.send(json.toString().getBytes(), 0);
        ZMsg msg = ZMsg.recvMsg(client);
        ZFrame content = msg.pop();
        assert (content != null);
        byte[] reply = content.getData();
        replyString = new String(reply);
        msg.destroy();

        JSONObject jsonResponse = new JSONObject(replyString);

        json = Json.createObjectBuilder()
                .add("method", "GET")
                .add("path", "/ensemble/services/supercollection")
                .build();
        client.send(json.toString().getBytes(), 0);
        msg = ZMsg.recvMsg(client);
        content = msg.pop();
        assert (content != null);
        reply = content.getData();
        replyString = new String(reply);
        msg.destroy();

        JSONObject jsonResponseSuper = new JSONObject(replyString);

        if (!jsonResponse.isNull("collections")) {
            JsonObject detectBestBanditCollectionJson = null;
            JSONArray collectionsArray = jsonResponse.getJSONArray("collections");
            detectBestBanditCollectionJson = Json.createObjectBuilder()
                    .add("method", "GET")
                    .add("path", "/ensemble/services/collection/" + collectionsArray.getInt(0) + "?filter=best")
                    .build();
            client.send(detectBestBanditCollectionJson.toString().getBytes(), 0);
            msg = ZMsg.recvMsg(client);
            content = msg.pop();
            assert (content != null);
            reply = content.getData();
            replyString = new String(reply);
            msg.destroy();

            JSONObject bestBanditCollectionJson = new JSONObject(replyString);
            boolean isBanditId = bestBanditCollectionJson.isNull("bestBandit");

            //Uzivatel si zvoli toho doporucovaneho banditu, zasle feedback o volbe    
            if (!isBanditId) {
                int bestBandit = bestBanditCollectionJson.getInt("bestBandit");
                String banditId = bestBanditCollectionJson.getString("banditId");
                int collectionId = bestBanditCollectionJson.getInt("collection");

                System.out.println(NAME + " is choosing bandit: " + bestBandit + " (" + banditId + ")");
                
                JsonObject sendUsefeedback = Json.createObjectBuilder()
                        .add("method", "PUT")
                        .add("path", "/ensemble/services/collection/" + collectionId + "/" + bestBandit)
                        .add("body", "operation=use")
                        .build();
                client.send(sendUsefeedback.toString().getBytes(), 0);
                msg = ZMsg.recvMsg(client);
                content = msg.pop();
                assert (content != null);
                reply = content.getData();
                replyString = new String(reply);
                msg.destroy();

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
                        //bestBanditCollectionJson = new JSONObject(userRatingFeedbackResp.readEntity(String.class));
                        //System.out.println(bestBanditCollectionJson.toString());
                        String feedback = "";

                        int randomChance = r.nextInt(100) + 1;
                        if (randomChance < 50) {
                            feedback = "possitive";
                        } else {
                            feedback = "negative";
                        }

                        System.out.println(NAME + "'s feedback: " + feedback);
                        
                        JsonObject sendFeedbackToEnsemble = Json.createObjectBuilder()
                                .add("method", "PUT")
                                .add("path", "/ensemble/services/collection/" + collectionId + "/" + bestBandit)
                                .add("body", "operation=feedback&feedbackType=" + feedback)
                                .build();
                        client.send(sendFeedbackToEnsemble.toString().getBytes(), 0);
                        msg = ZMsg.recvMsg(client);
                        content = msg.pop();
                        assert (content != null);
                        reply = content.getData();
                        replyString = new String(reply);
                        msg.destroy();
                    }

                }
            }

        }
        /*
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
         */
        ctx.destroy();
    }
}
