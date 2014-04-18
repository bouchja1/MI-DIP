/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditCollectionDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.zeromq.EnsembleZeroMqHelper;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 * http://www.mkyong.com/webservices/jax-rs/jersey-spring-integration-example/
 * http://localhost:8089/rest/ensemble/JavaCodeGeeks?value=enjoy-REST
 */
@Component
@Path(EnsembleEndpoint.ENDPOINT_PATH)
public class EnsembleEndpoint {
    
    public static final String ENDPOINT_PATH = "/ensemble";
    public static final String COLLECTION_PATH = "/collection";
    public static final String COLLECTION_ID = "/{collectionId}";

    @Autowired
    private EnsembleZeroMqHelper ensembleZeroMqHelper;

    @Path(COLLECTION_PATH)
    @POST
    //@Consumes("application/x-www-form-urlencoded")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createBanditCollection(BanditCollectionDocument banditCollection) {
        return ensembleZeroMqHelper.createBanditSet(banditCollection);
    }

    @Path(COLLECTION_PATH + COLLECTION_ID)
    @GET   
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getBanditCollection(@PathParam(value="collectionId") String collectionId, @QueryParam(value = "filter") String filter) {
        return ensembleZeroMqHelper.filterBanditCollection(collectionId, filter);
    }
    
    public Response sendBanditPull() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Response sendFeedback() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    } 
}
