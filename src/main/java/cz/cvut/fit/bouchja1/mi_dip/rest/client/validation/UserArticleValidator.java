/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.validation;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.UserArticle;
import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public class UserArticleValidator {
    public static String validateUserArticle(UserArticle userArticle) {
        return "success";    
    }
}
