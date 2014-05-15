/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditCollection;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditSuperCollection;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.EnsembleOperation;
import static cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint.EnsembleEndpointSupercollectionImpl.SUPERCOLLECTION_PATH;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.zeromq.EnsembleZeroMqHelper;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 * http://www.mkyong.com/webservices/jax-rs/jersey-spring-integration-example/
 * http://localhost:8089/rest/ensemble/JavaCodeGeeks?value=enjoy-REST
 */
@Component
@Path(EnsembleEndpointSupercollectionImpl.SUPERCOLLECTION_PATH)
public class EnsembleEndpointSupercollectionImpl implements EnsembleEndpointSupercollection {
    
    public static final String SUPERCOLLECTION_PATH = "/supercollection";
    public static final String SUPERCOLLECTION_ID = "/{supercollectionId}";    

    @Autowired
    private EnsembleZeroMqHelper ensembleZeroMqHelper;
    
    @Context
    UriInfo uriInfo;    
    
    @POST    
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})    
    @Override
    public Response createBanditSuperCollection(BanditSuperCollection banditSuperCollection) {
        return ensembleZeroMqHelper.createBanditSuperSet(banditSuperCollection, uriInfo);
    }    

    @GET   
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public Response getBanditSuperCollections() {
        return ensembleZeroMqHelper.getAllBanditSuperCollections();
    }      
     
    @Path(SUPERCOLLECTION_ID)
    @GET   
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public Response getBestBanditSuperCollection(@PathParam(value="supercollectionId") String supercollectionId, @QueryParam(value = "filter") String filter) {
        return ensembleZeroMqHelper.filterBestBanditSuperCollection(supercollectionId, filter);
    }        
    
    @Path(SUPERCOLLECTION_ID)
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})  
    @Override
    public Response sendEnsembleOperationToSuperCollection(@PathParam(value="supercollectionId") String supercollectionId, EnsembleOperation ensembleOperation) {
        return ensembleZeroMqHelper.sendEnsembleOperationToSuperCollection(supercollectionId, ensembleOperation);
    }    
}