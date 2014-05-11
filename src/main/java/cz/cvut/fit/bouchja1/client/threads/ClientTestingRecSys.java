/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.threads;

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
public class ClientTestingRecSys extends Thread {

    private int id;
    private Communication communication;

    public ClientTestingRecSys(int name, Communication communication) {
        this.id = name;
        this.communication = communication;
    }

    public void run() {
        System.out.println("Client " + id + " - communication started");        
        
        //ted si necha doporucit tim banditou obsah
        //String algorithmToUse = bestChoiceFromSuperCollection2.getString("banditId");        
        Map<String, String> parameters = new HashMap<>();
        String algorithmToUse = "random";
        //String algorithmToUse = "latest";
        //String algorithmToUse = "toprate";
        //String algorithmToUse = "mlt";
        //String algorithmToUse = "cfuser";

        parameters.put("limit", "15");
        parameters.put("userId", id+"");
        parameters.put("documentId", "f7lmhi2xwb00m74v1gi ");
        Response algorithmRecommendation = communication.getRecommendationByAlgorithm(algorithmToUse, parameters);
        System.out.println(algorithmRecommendation.toString());    
        
    }
}
