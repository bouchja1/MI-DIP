/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.OutputDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.AlgorithmEndpointHelper;
import java.util.List;
import javax.ws.rs.GET;
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
@Path(AlgorithmEndpoint.ENDPOINT_PATH)
public class AlgorithmEndpoint {

    public static final String ENDPOINT_PATH = "/algorithm";
    public static final String ALGORITHM_RANDOM_PATH = "/{coreId}/random";
    public static final String ALGORITHM_LATEST_PATH = "/{coreId}/latest";
    public static final String ALGORITHM_MLT_ID_PATH = "/{coreId}/morelikethisid";
    public static final String ALGORITHM_MLT_TEXT_PATH = "/{coreId}/morelikethistext";
    
    private AlgorithmEndpointHelper algorithmEndpointHelper;

    @Path(ALGORITHM_RANDOM_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @GET
    public Response recommendRandom(@PathParam("coreId") String coreId, @QueryParam(value = "groupId") int groupId, @QueryParam(value = "limit") int limit) {    
        return algorithmEndpointHelper.getRecommendationByRandom(coreId, groupId, limit);
    }
    
    @Path(ALGORITHM_LATEST_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @GET
    public Response recommendLatest(@PathParam("coreId") String coreId, @QueryParam(value = "groupId") int groupId, @QueryParam(value = "limit") int limit) {    
        return algorithmEndpointHelper.getRecommendationByLatest(coreId, groupId, limit);
    }    
    
    /*
     * MLT ID
     */
    @Path(ALGORITHM_MLT_ID_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @GET
    public Response recommendMltId(@PathParam("coreId") String coreId, @QueryParam(value = "documentId") String documentId, @QueryParam(value = "limit") int limit) {    
        return algorithmEndpointHelper.getRecommendationByMltId(coreId, documentId, limit);
    }      
    
    /*
     * MLT TEXT
     */    
    @Path(ALGORITHM_MLT_TEXT_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @GET
    public Response recommendMltText(@PathParam("coreId") String coreId, @QueryParam(value = "text") String text, @QueryParam(value = "limit") int limit) {    
        return algorithmEndpointHelper.getRecommendationByMltText(coreId, text, limit);
    }      
    
    /*
     * MLT TOP RATED
     */    
    
    /*
     * MLT HYBRID
     */    
    
    /*
     * MLT CF ITEM SOLR
     */    
    
    /*
     * MLT CF USER MAHOUT
     */        
    
    /*
     * MLT CF ITEM MAHOUT
     */       
    
    /*
     * MLT CF USER RATING ITEM
     */            

    public void setAlgorithmEndpointHelper(AlgorithmEndpointHelper algorithmEndpointHelper) {
        this.algorithmEndpointHelper = algorithmEndpointHelper;
    }
}
