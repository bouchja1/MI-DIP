/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.UserArticleDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.CoresEndpointHelper;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 */
@Component
@Path(CoresEndpoint.ENDPOINT_PATH)
public class CoresEndpoint {

    public static final String ENDPOINT_PATH = "/cores";
    public static final String USER_ARTICLE_PATH = "/{coreId}/document";
    
    private CoresEndpointHelper coresEndpointHelper;  
    
    @Path(USER_ARTICLE_PATH)
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @POST
    public Response insertUpdateUserArticle(@PathParam("coreId") String coreId, UserArticleDocument userArticle) {
        return coresEndpointHelper.putUserArticle(coreId, userArticle);
    }    
    
    /*
    @Path(USER_ARTICLE_PATH)
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes("application/x-www-form-urlencoded")
    @POST
    public Response insertUpdateUserArticle(@PathParam("coreId") String coreId, MultivaluedMap queryParams) {
        return coresEndpointHelper.putUserArticle(coreId, queryParams);
    } 
    */
    
    //curl -X DELETE 'http://localhost:8089/ensembleRestApi/recommeng/cores/userBased/document?documentId=sdsfsdsdf'
    @Path(USER_ARTICLE_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @DELETE
    public Response disableArticle(@PathParam("coreId") String coreId, @QueryParam("documentId") String documentId) {
        return coresEndpointHelper.disableArticle(coreId, documentId);
    }    

    public void setCoresEndpointHelper(CoresEndpointHelper coresEndpointHelper) {
        this.coresEndpointHelper = coresEndpointHelper;
    }
    
    
}
