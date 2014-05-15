/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.tools;

import cz.cvut.fit.bouchja1.client.crate.Article;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import org.apache.solr.schema.DateField;
import java.util.Set;
import org.apache.solr.common.SolrInputDocument;
import org.fluttercode.datafactory.impl.DataFactory;

/**
 *
 * @author jan
 */
public class ArticleGenerator {

    private List<Article> articles = new ArrayList<>();
    private static final int NUMBER_OF_DOCUMENTS = 5000;
    private static final String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ARTICLE_ID_LENGTH = 50;
    private static final int ARTICLE_TEXT_LENGTH = 800;
    private static final int maxNumberOfUsersToArticle = 100;
    private int[] groupIds = new int[]{123, 345, 567, 789};
    private int[] userIds = new int[100];
    private Random rng = new Random();
    private DataFactory df = new DataFactory();
    
    private Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    private Collection<SolrInputDocument> docsBehavioral = new ArrayList<SolrInputDocument>();
         

    public void generate() {
        for (int i = 0; i < NUMBER_OF_DOCUMENTS; i++) {
            //fillArrayWithUsers();
            Article article = generateDocumentAndBehavioral();
            articles.add(article);        
            prepareDocument(article, (i+1));
            prepareDocumentBehavioral(article, (i+1));            
        }
    }

    private Article generateDocumentAndBehavioral() {
        Article a = new Article();
        generateArticleId(a);
        generateArticleText(a);
        generateGroupId(a);
        generateUsers(a);
        generateRating(a);
        generateTime(a);
        return a;
    }

    private void generateUsers(Article a) {
        int usersToArticle = rng.nextInt(maxNumberOfUsersToArticle) + 1; //kolik useru bude generovano ke clanku
        Set<Integer> usersList = new HashSet<>();
        for (int i = 0; i < usersToArticle; i++) {
            usersList.add(rng.nextInt(userIds.length) + 1);
        }
        a.setUsers(usersList);
    }

    private void generateRating(Article a) {
        Random r = new Random();
        int rating = rng.nextInt(5) + 1;
        double randomValue = (double) rating;
        a.setRating(randomValue);
    }

    private void generateArticleId(Article a) {
        char[] id = new char[ARTICLE_ID_LENGTH];
        for (int i = 0; i < ARTICLE_ID_LENGTH; i++) {
            id[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        String ida = new String(id);
        a.setId("http://" + ida + ".org");
    }

    private void generateArticleText(Article a) {
        String text = df.getRandomText(ARTICLE_TEXT_LENGTH);
        a.setText(text);
    }

    private void generateGroupId(Article a) {
        a.setGroupId(groupIds[rng.nextInt(groupIds.length)]);
    }

    private void generateTime(Article a) {
        long offset = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
        long end = Timestamp.valueOf("2013-01-01 00:00:00").getTime();
        long diff = end - offset + 1;
        Timestamp rand = new Timestamp(offset + (long) (Math.random() * diff));
        long timeMilis = rand.getTime();
        Date d = new Date(timeMilis);
        a.setTime(d);
    }

    public List<Article> getArticles() {
        return articles;
    }
    
    public void prepareDocument(Article a, int id) {
        SolrInputDocument doc1 = new SolrInputDocument();
        //doc1.addField("id", "id1", 1.0f); ten treti parametr je boost
        doc1.addField("id", id);
        doc1.addField("articleId", a.getId());
        doc1.addField("articleText", a.getText());
        doc1.addField("group", a.getGroupId());
        //doc1.addField("userId", a.getUsers());
        //doc1.addField("rating", a.getRating());
        doc1.addField("time", DateField.formatExternal(a.getTime()));
        //doc1.addField("impressions", 0);
        docs.add(doc1);
    }

    public void prepareDocumentBehavioral(Article a, int id) {
        SolrInputDocument doc1 = new SolrInputDocument();
        //doc1.addField("id", "id1", 1.0f); ten treti parametr je boost
        doc1.addField("id", id);
        doc1.addField("articleId", a.getId());
        doc1.addField("group", a.getGroupId());
        doc1.addField("userId", a.getUsers());
        doc1.addField("weightedRating", a.getRating());
        
        for (Integer userId : a.getUsers()) {
            int rating  = rng.nextInt(5) + 1;
            double randomValue = (double) rating;
            doc1.addField(userId + "_rating", randomValue);
        }        
     
        docsBehavioral.add(doc1);
    }     

    public Collection<SolrInputDocument> getDocs() {
        return docs;
    }

    public Collection<SolrInputDocument> getDocsBehavioral() {
        return docsBehavioral;
    }
    
    
}
