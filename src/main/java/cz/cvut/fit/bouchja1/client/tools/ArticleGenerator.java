/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.tools;

import cz.cvut.fit.bouchja1.client.crate.Article;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 *
 * @author jan
 */
public class ArticleGenerator {

    private List<Article> articles = new ArrayList<>();
    private static final int NUMBER_OF_DOCUMENTS = 1000;
    private static final String characters = "abcdefghijklmnopqrstuvwxyz 0123456789";
    private static final int ARTICLE_ID_LENGTH = 20;
    private static final int ARTICLE_TEXT_LENGTH = 800;
    private int[] groupIds = new int[]{123, 345, 567, 789};
    //private int[] userIds = new int[100];
    private Random rng = new Random();

    public void generate() {
        for (int i = 0; i < NUMBER_OF_DOCUMENTS; i++) {
            //fillArrayWithUsers();
            Article article = generateDocument();
            articles.add(article);
        }
    }
    
    private Article generateDocument() {
        Article a = new Article();
        generateArticleId(a);
        generateArticleText(a);
        generateGroupId(a);
        generateTime(a);
        return a;
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
        char[] text = new char[ARTICLE_TEXT_LENGTH];
        for (int i = 0; i < ARTICLE_TEXT_LENGTH; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        
        a.setText(new String(text));
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
    
    
}
