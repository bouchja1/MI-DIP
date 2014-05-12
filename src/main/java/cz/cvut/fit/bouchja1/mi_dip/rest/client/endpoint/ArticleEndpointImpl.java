/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.AlgorithmFactory;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.ArticleDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.UserArticleDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.ArticleEndpointHelper;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 */
@Component
@Path(ArticleEndpointImpl.ENDPOINT_PATH)
public class ArticleEndpointImpl implements ArticleEndpoint {

    public static final String ENDPOINT_PATH = "/cores";
    public static final String USER_ARTICLE_PATH = "/{coreId}/documents";
    @Autowired
    private ArticleEndpointHelper articleEndpointHelper;

    @Path(USER_ARTICLE_PATH)
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @PUT
    @Override
    public Response updateBehavioralToArticle(@PathParam("coreId") String coreId, UserArticleDocument userArticle) {
        return articleEndpointHelper.updateBehavioralToArticle(coreId, userArticle);
    }

    //curl -X DELETE 'http://localhost:8089/ensembleRestApi/recommeng/cores/userBased/document?documentId=sdsfsdsdf'
    @Path(USER_ARTICLE_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @DELETE
    @Override
    public Response deleteDocument(@PathParam("coreId") String coreId, @QueryParam("documentId") String documentId) {
        return articleEndpointHelper.disableArticleFromRecommendation(coreId, documentId);
    }

    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @GET
    @Override
    public Response getSupportedCores() {
        Set<String> cores = articleEndpointHelper.getSolrService().getValidSolrCores();
        return Response.ok(
                new GenericEntity<Set<String>>(cores) {
        }).build();
    }

    /* A list of resources provided in json format will be added
     * to the database.    
     */
    @Override
    @Path(USER_ARTICLE_PATH)
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @POST
    public Response postArticles(@PathParam("coreId") String coreId, ArticleDocument article) {
        return articleEndpointHelper.postArticle(coreId, article);
    }

    public void setArticleEndpointHelper(ArticleEndpointHelper articleEndpointHelper) {
        this.articleEndpointHelper = articleEndpointHelper;
    }
}
