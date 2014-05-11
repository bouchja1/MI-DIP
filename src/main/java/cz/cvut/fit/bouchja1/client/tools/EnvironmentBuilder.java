/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.tools;

import cz.cvut.fit.bouchja1.client.api.Communication;
import cz.cvut.fit.bouchja1.client.crate.Article;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;

/**
 *
 * @author jan
 */
public class EnvironmentBuilder {

    private Communication communication;
    private ArticleGenerator articleGenerator = new ArticleGenerator();
    private Random rng = new Random();   

    public EnvironmentBuilder(Communication communication) {
        this.communication = communication;
    }
    
    public void createTestArticles(String articleCore) {
        List<JsonObject> collectionOfArticles = new ArrayList<>();
        //JSONArray jsonArrayArticles = new JSONArray();
        articleGenerator.generate();
        List<Article> generatedArticles = articleGenerator.getArticles();

        for (Article a : generatedArticles) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String reportDate = sdf.format(a.getTime());
            JsonObject article = Json.createObjectBuilder()
                    .add("id", a.getId())
                    .add("text", a.getText())
                    .add("groupId", a.getGroupId())
                    .add("time", reportDate)
                    .build();

            collectionOfArticles.add(article);
        }
        communication.createArticlesInCore(collectionOfArticles, articleCore);
    }

    public void createTestArticlesAndBehavior(String articleCore, String behavioralCore) {
        List<JsonObject> collectionOfArticles = new ArrayList<>();
        //JSONArray jsonArrayArticles = new JSONArray();
        JSONArray jsonArrayBehavioral = new JSONArray();
        articleGenerator.generate();
        List<Article> generatedArticles = articleGenerator.getArticles();

        for (Article a : generatedArticles) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String reportDate = sdf.format(a.getTime());
            JsonObject article = Json.createObjectBuilder()
                    .add("id", a.getId())
                    .add("text", a.getText())
                    .add("groupId", a.getGroupId())
                    .add("time", reportDate)
                    .build();

            JsonArrayBuilder arrayUserBuilder = Json.createArrayBuilder();
            for (Integer user : a.getUsers()) {
                arrayUserBuilder.add(user);
            }

            JsonObjectBuilder articleBehavioral = Json.createObjectBuilder()
                    .add("id", a.getId())
                    .add("groupId", a.getGroupId())
                    .add("time", reportDate)
                    .add("userId", arrayUserBuilder)
                    .add("weightedRating", a.getRating());

            for (Integer userId : a.getUsers()) {
                int rating = rng.nextInt(5) + 1;
                double randomValue = (double) rating;
                articleBehavioral.add(userId + "_rating", randomValue);
            }
            collectionOfArticles.add(article);
            jsonArrayBehavioral.put(articleBehavioral.build());
        }

        communication.createArticlesInCore(collectionOfArticles, articleCore);
        communication.createBehavioralInCore(jsonArrayBehavioral, behavioralCore);
    }

    public void fillIndexWithTestData(String serverLocation, String articleCore, String behavioralCore) {
        SolrServer server = new HttpSolrServer(serverLocation + articleCore);
        SolrServer serverBehavioral = new HttpSolrServer(serverLocation + behavioralCore);        
        articleGenerator.generate();
        try {
            server.add(articleGenerator.getDocs());
            serverBehavioral.add(articleGenerator.getDocsBehavioral());
            server.commit();
            serverBehavioral.commit();
        } catch (SolrServerException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }          
    }
}
