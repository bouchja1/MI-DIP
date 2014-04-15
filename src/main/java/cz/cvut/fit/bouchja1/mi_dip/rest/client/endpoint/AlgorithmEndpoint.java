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
    
    private AlgorithmEndpointHelper algorithmEndpointHelper;

    @Path(ALGORITHM_RANDOM_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @GET
    public Response recommendRandom(@PathParam("coreId") String coreId, @QueryParam(value = "limit") int limit) {    
        return algorithmEndpointHelper.getRecommendationByRandom(coreId, limit);
    }

    public void setAlgorithmEndpointHelper(AlgorithmEndpointHelper algorithmEndpointHelper) {
        this.algorithmEndpointHelper = algorithmEndpointHelper;
    }
}
