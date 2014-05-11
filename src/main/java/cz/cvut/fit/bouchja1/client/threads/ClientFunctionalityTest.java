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

        //test ulozeni do indexu generatorem


        /*
         * test Algorithm endpoint
         */
        //GET /algorithm
        Response algorithmsList = communication.getAlgorithmsList();

        if (algorithmsList.getStatus() != 200) {
            System.out.println("Chyba pri vypisu algoritmu: " + algorithmsList.getStatus());
        } else {
            JSONArray algorithmArray = new JSONArray(algorithmsList.readEntity(String.class));

            Random r = new Random();
            int randomAlgorithmIndex = r.nextInt(algorithmArray.length());
            String algorithm = algorithmArray.getString(randomAlgorithmIndex);

            //GET cores
            Response coresList = communication.getCoresList();
            if (coresList.getStatus() != 200) {
                System.out.println("Chyba pri vypisu cores: " + coresList.getStatus());
            } else {
                JSONArray coresArray = new JSONArray(coresList.readEntity(String.class));
                //JSONArray coresArray = coresList.getJSONArray("cores");
                r = new Random();
                int randomCoreIndex = r.nextInt(coresArray.length());
                String core = coresArray.getString(randomCoreIndex);

                //GET /algorithm/{algorithm-id}

                Map<String, String> parameters = new HashMap<>();
                parameters.put("coreId", core);

                Response recommendedArticles;

                switch (algorithm) {
                    case "random":
                        //TODO to plneni parametru bude tady
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    case "latest":
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    case "mlt":
                        parameters.put("documentId", "http://80wabvelo7l514atqwmelko0xjzyj4m62p2taa45jfsm8kxryn42399ji2yx0rj422xb4dyyah7gj6eq64boz4havxd3wigajs3dnqlvn5wr4fgbmctr2z0y4nh4nl0jijr7mf0ka686yhgtyu3pmothvo6b5c8zre0x16g6wtruyu049r545pc26twlu7z25dvm8e8ftxtwm8fsjyzwqd6puuz6ne6spt1bbiw5pn6xfpeqmk0z4ba72qrmetrujuksrklzjnuombb9g4yg2xkjv644dw5bqzt5n7um4f0n6gid1vn6125x1xiin7h4fpkijic90a6zvnynnh1q22hqfnoa8jlv2v9u3resaamcpffmb16resik6e4wgi1ima06vpoffjeekymq.org");
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    case "toprate":
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    case "cfuser":
                        recommendedArticles = communication.getRecommendationByAlgorithm(algorithm, parameters);
                        break;
                    case "cfitem":
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
                        System.out.println(mainObjArticle.toString());
                    } else {
                        System.out.println("Chyba pri vypisu clanku");
                    }

                } else {
                    System.out.println("Client did not recognized algorithm type");
                }
            }
        }
    }
}
