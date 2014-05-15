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
 */
public class ClientFunctionalityTest extends Thread {

    private int id;
    private Communication communication;

    public ClientFunctionalityTest(int name, Communication communication) {
        this.id = name;
        this.communication = communication;
    }

    /*
     * Test na vraceni status codu (protestovani API dle dokumentace).
     */
    public void run() {

        /*
         * test Algorithm endpoint
         */
        //GET /algorithm
        Response algorithmsList = communication.getAlgorithmsList();

        if (algorithmsList.getStatus() != 200) {
            System.out.println("Chyba pri vypisu algoritmu: " + algorithmsList.getStatus());
            JSONObject jo = new JSONObject(algorithmsList.readEntity(String.class));
            System.out.println(jo.toString());
        } else {
            JSONArray algorithmArray = new JSONArray(algorithmsList.readEntity(String.class));
            System.out.println("Vypis GET /algorithm endpointu: " + algorithmArray.toString());
            Random r = new Random();
            int randomAlgorithmIndex = r.nextInt(algorithmArray.length());
            String algorithm = algorithmArray.getString(randomAlgorithmIndex);

            //GET cores
            Response coresList = communication.getCoresList();
            if (coresList.getStatus() != 200) {
                System.out.println("Chyba pri vypisu cores: " + coresList.getStatus());
                JSONObject jo = new JSONObject(coresList.readEntity(String.class));
                System.out.println(jo.toString());
            } else {
                JSONArray coresArray = new JSONArray(coresList.readEntity(String.class));
                //JSONArray coresArray = coresList.getJSONArray("cores");
                System.out.println("Vypis GET /cores endpointu: " + coresArray.toString());
                /*
                r = new Random();
                int randomCoreIndex = r.nextInt(coresArray.length());
                String core = coresArray.getString(randomCoreIndex);
                */
                
                //GET /algorithm/{algorithm-id}

                Map<String, String> parameters = new HashMap<>();                
                parameters.put("userId", id+"");

                Response recommendedArticles;

                switch (algorithm) {
                    case "random":
                        //TODO to plneni parametru bude tady
                        parameters.put("coreId", "articleCore");
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    case "latest":
                        parameters.put("coreId", "articleCore");
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    case "mlt":
                        parameters.put("coreId", "articleCore");
                        parameters.put("documentId", "http://26vkzcvjsfgg0z8e20hi4cvdrykbemyt7ybapoj6f1asyn52i6.org");
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    case "toprate":
                        parameters.put("coreId", "behavioralCore");
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    case "cfuser":
                        parameters.put("coreId", "behavioralCore");
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    case "cfitem":
                        parameters.put("coreId", "behavioralCore");
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    default:
                        recommendedArticles = null;
                }

                JSONObject mainObjArticle = new JSONObject();
                if (recommendedArticles != null) {
                    if (recommendedArticles.getStatus() == 200) {
                        //JSONObject mainObj = new JSONObject();
                        JSONArray ja = new JSONArray(recommendedArticles.readEntity(String.class));
                        mainObjArticle.put("articles", ja);
                        System.out.println("Vypis doporucenych clanku algoritmem: " + algorithm);
                        System.out.println(mainObjArticle.toString());
                    } else {
                        System.out.println("Chyba pri vypisu clanku, status: " + recommendedArticles.getStatus() + ", algoritmus: " + algorithm);
                        if (recommendedArticles.getStatus() == 400) {
                            JSONObject jo = new JSONObject(recommendedArticles.readEntity(String.class));
                            System.out.println(jo.toString());
                        }
                    }

                } else {
                    System.out.println("Client did not recognized algorithm type");
                }
            }
        }
    }
}
