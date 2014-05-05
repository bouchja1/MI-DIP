/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.tools;

import cz.cvut.fit.bouchja1.client.api.Communication;
import cz.cvut.fit.bouchja1.client.crate.Article;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author jan
 */
public class EnvironmentBuilder {

    private Communication communication;
    private ArticleGenerator articleGenerator = new ArticleGenerator();

    public EnvironmentBuilder(Communication communication) {
        this.communication = communication;
    }

    public void build() {
        createTestArticles();
    }

    private void createTestArticles() {
        List<JsonObject> collectionOfArticles = new ArrayList<>();
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

        communication.createArticlesInCore(collectionOfArticles);
    }
}
