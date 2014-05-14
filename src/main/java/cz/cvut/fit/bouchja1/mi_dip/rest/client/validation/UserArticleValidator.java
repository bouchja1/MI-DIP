package cz.cvut.fit.bouchja1.mi_dip.rest.client.validation;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.UserArticleDocument;
import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public class UserArticleValidator {
    public static String validateUserArticle(UserArticleDocument userArticle) {
        int userId = userArticle.getUserId();
        String articleId = userArticle.getArticleId();
        double rating = userArticle.getRating();
        
        if ((userId == 0) || (articleId == null) || (rating == 0)) {
            return "empty";
        } else return "success";    
    }
}
