/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.ArticleDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.UserArticleDocument;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public interface ArticleEndpoint {
    public Response getSupportedCores();    
    public Response updateBehavioralToArticle(String coreId, UserArticleDocument userArticle);  
    public Response postArticles(String coreId, ArticleDocument articles);
    public Response deleteDocument(String coreId, String documentId);      
}
