/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditCollection;
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
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 */
@Component
@Path(EnsembleEndpointCollectionImpl.COLLECTION_PATH)
public class EnsembleEndpointCollectionImpl implements EnsembleEndpointCollection {
    @Autowired
    private EnsembleZeroMqHelper ensembleZeroMqHelper;
    
    public static final String COLLECTION_PATH = "/collection";
    public static final String COLLECTION_ID = "/{collectionId}";    

    @POST
    //@Consumes("application/x-www-form-urlencoded")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public Response createBanditCollection(BanditCollection banditCollection) {
        return ensembleZeroMqHelper.createBanditSet(banditCollection);
    }
    
    @GET   
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public Response getBanditCollections() {
        return ensembleZeroMqHelper.getAllBanditCollections();
    }    
    
    @Path(COLLECTION_ID)
    @GET   
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    //MA TAM CENU TEN FILTER?
    @Override
    public Response getBestBanditCollection(@PathParam(value="collectionId") String collectionId, @QueryParam(value = "filter") String filter) {
        return ensembleZeroMqHelper.filterBanditCollection(collectionId, filter);
    }
    
    @Path(COLLECTION_ID)
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})  
    @Override
    public Response sendEnsembleOperationToCollection(@PathParam(value="collectionId") String collectionId, EnsembleOperation ensembleOperation) {
        return ensembleZeroMqHelper.sendEnsembleOperationToCollection(collectionId, ensembleOperation);
    }    
}
