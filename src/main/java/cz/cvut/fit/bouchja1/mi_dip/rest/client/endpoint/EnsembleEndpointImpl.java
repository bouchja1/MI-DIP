/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditCollection;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditSuperCollection;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.EnsembleOperation;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.zeromq.EnsembleZeroMqHelper;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
 * http://www.mkyong.com/webservices/jax-rs/jersey-spring-integration-example/
 * http://localhost:8089/rest/ensemble/JavaCodeGeeks?value=enjoy-REST
 */
@Component
@Path(EnsembleEndpointImpl.ENDPOINT_PATH)
public class EnsembleEndpointImpl implements EnsembleEndpoint {
    
    public static final String ENDPOINT_PATH = "/ensemble";
    public static final String SUPERCOLLECTION_PATH = "/supercollection";
    public static final String SUPERCOLLECTION_ID = "/{supercollectionId}";    
    public static final String COLLECTION_PATH = "/collection";
    public static final String COLLECTION_ID = "/{collectionId}";

    @Autowired
    private EnsembleZeroMqHelper ensembleZeroMqHelper;

    @Path(COLLECTION_PATH)
    @POST
    //@Consumes("application/x-www-form-urlencoded")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public Response createBanditCollection(BanditCollection banditCollection) {
        return ensembleZeroMqHelper.createBanditSet(banditCollection);
    }
    
    @Path(SUPERCOLLECTION_PATH)
    @POST    
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})    
    @Override
    public Response createBanditSuperCollection(BanditSuperCollection banditSuperCollection) {
        return ensembleZeroMqHelper.createBanditSuperSet(banditSuperCollection);
    }    
    
    @Path(COLLECTION_PATH)
    @GET   
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public Response getBanditCollections() {
        return ensembleZeroMqHelper.getAllBanditCollections();
    }    

    @Path(SUPERCOLLECTION_PATH)
    @GET   
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public Response getBanditSuperCollections() {
        return ensembleZeroMqHelper.getAllBanditSuperCollections();
    }      
    
    @Path(COLLECTION_PATH + COLLECTION_ID)
    @GET   
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    //MA TAM CENU TEN FILTER?
    @Override
    public Response getBestBanditCollection(@PathParam(value="collectionId") String collectionId, @QueryParam(value = "filter") String filter) {
        return ensembleZeroMqHelper.filterBanditCollection(collectionId, filter);
    }
     
    @Path(SUPERCOLLECTION_PATH + SUPERCOLLECTION_ID)
    @GET   
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public Response getBestBanditSuperCollection(@PathParam(value="supercollectionId") String supercollectionId, @QueryParam(value = "filter") String filter) {
        return ensembleZeroMqHelper.filterBestBanditSuperCollection(supercollectionId, filter);
    }        
    
    @Path(COLLECTION_PATH + COLLECTION_ID)
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})  
    @Override
    public Response sendEnsembleOperationToCollection(@PathParam(value="collectionId") String collectionId, EnsembleOperation ensembleOperation) {
        return ensembleZeroMqHelper.sendEnsembleOperationToCollection(collectionId, ensembleOperation);
    }
    
    @Path(SUPERCOLLECTION_PATH + SUPERCOLLECTION_ID)
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})  
    @Override
    public Response sendEnsembleOperationToSuperCollection(@PathParam(value="supercollectionId") String supercollectionId, EnsembleOperation ensembleOperation) {
        return ensembleZeroMqHelper.sendEnsembleOperationToSuperCollection(supercollectionId, ensembleOperation);
    }    
}