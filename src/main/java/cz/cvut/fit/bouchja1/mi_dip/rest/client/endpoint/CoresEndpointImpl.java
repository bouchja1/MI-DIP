/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.ArticleDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.UserArticleDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.CoresEndpointHelper;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 */
@Component
@Path(CoresEndpointImpl.ENDPOINT_PATH)
public class CoresEndpointImpl implements CoresEndpoint {

    public static final String ENDPOINT_PATH = "/cores";
    public static final String USER_ARTICLE_PATH = "/{coreId}/document";
    
    private CoresEndpointHelper coresEndpointHelper;  
    
    @Path(USER_ARTICLE_PATH)
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @POST
    @Override
    public Response updateBehavioralToArticle(@PathParam("coreId") String coreId, UserArticleDocument userArticle) {
        return coresEndpointHelper.updateBehavioralToArticle(coreId, userArticle);
    }    
    
    //curl -X DELETE 'http://localhost:8089/ensembleRestApi/recommeng/cores/userBased/document?documentId=sdsfsdsdf'
    @Path(USER_ARTICLE_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @DELETE
    @Override
    public Response disableArticleFromRecommendation(@PathParam("coreId") String coreId, @QueryParam("documentId") String documentId) {
        return coresEndpointHelper.disableArticleFromRecommendation(coreId, documentId);
    }    

    public void setCoresEndpointHelper(CoresEndpointHelper coresEndpointHelper) {
        this.coresEndpointHelper = coresEndpointHelper;
    }

    @Override
    public Response postArticle(@PathParam("coreId") String coreId, ArticleDocument article) {
        return coresEndpointHelper.postArticle(coreId, article);
    }

    @Override
    public Response postArticles(@PathParam("coreId") String coreId, List<ArticleDocument> articles) {
        return coresEndpointHelper.postArticles(coreId, articles);
    }
    
    
}
