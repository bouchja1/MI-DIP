/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.UserArticleDocument;
import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public interface CoresEndpoint {
    public Response insertUpdateUserArticle(String coreId, UserArticleDocument userArticle);   
    public Response disableArticle(String coreId, String documentId);      
}
