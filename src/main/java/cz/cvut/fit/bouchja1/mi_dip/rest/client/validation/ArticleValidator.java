/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.validation;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.ArticleDocument;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jan
 */
public class ArticleValidator {

    public static String validateArticle(ArticleDocument article) {
        String id = article.getId();
        String text = article.getText();
        int groupId = article.getGroupId();
        Date time = article.getTime();

        if ((id == null) || (text == null) || (groupId == 0) || (time == null)) {
            return "empty";
        }

        try {
            new Date(time.getTime());
        } catch (Exception ex) {
            return "badDateFormat";
        }

        return "success";
    }

    public static String validateArticle(List<ArticleDocument> article) {
        for (ArticleDocument a : article) {
            if (!"success".equals(validateArticle(a))) {
                return "badFormatArticles";
            }
        }
        return "success";
    }
}
