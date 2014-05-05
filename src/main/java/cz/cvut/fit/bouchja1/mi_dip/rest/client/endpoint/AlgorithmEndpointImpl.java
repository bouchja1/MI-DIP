/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.AlgorithmFactory;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.IAlgorithm;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.AlgorithmEndpointHelper;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
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
@Path(AlgorithmEndpointImpl.ENDPOINT_PATH)
public class AlgorithmEndpointImpl implements AlgorithmEndpoint {

    public static final String ENDPOINT_PATH = "/algorithm";
    public static final String ALGORITHM_PATH = "/{coreId}/{algorithmId}";
    
    @Autowired
    private AlgorithmEndpointHelper algorithmEndpointHelper;

    @Path(ALGORITHM_PATH)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @GET
    @Override
    public Response recommend(@PathParam("coreId") String coreId,
            @PathParam("algorithmId") String algorithmId,
            @QueryParam(value = "groupId") int groupId,
            @QueryParam(value = "userId") int userId,
            @QueryParam(value = "documentId") String documentId,
            @QueryParam(value = "text") String text,
            @QueryParam(value = "limit") int limit) {
        
        Map<String, String> algorithmParams = createAlgorithmParams(coreId, algorithmId, groupId, userId, documentId, text, limit);
        IAlgorithm algorithm = AlgorithmFactory.getAlgorithm(algorithmId, algorithmParams);
        
        if (algorithm == null) {
            return algorithmEndpointHelper.createAlgorithmNotFound();
        }
        else return algorithmEndpointHelper.getRecommendation(algorithm);
    }

    public void setAlgorithmEndpointHelper(AlgorithmEndpointHelper algorithmEndpointHelper) {
        this.algorithmEndpointHelper = algorithmEndpointHelper;
    }

    private Map<String, String> createAlgorithmParams(String coreId, String algorithmId, int groupId, int userId, String documentId, String text, int limit) {
        Map<String, String> algorithmParams = new HashMap<>();
        if (coreId != null) {
            algorithmParams.put("coreId", coreId);
        }
        if (algorithmId != null) {
            algorithmParams.put("algorithmId", algorithmId);
        }  
        if (groupId != 0) {
            algorithmParams.put("groupId", String.valueOf(groupId));
        }  
        if (userId != 0) {
            algorithmParams.put("userId", String.valueOf(userId));
        }            
        if (documentId != null) {
            algorithmParams.put("documentId", documentId);
        }  
        if (text != null) {
            algorithmParams.put("text", text);
        }           
        if (limit != 0) {
            algorithmParams.put("limit", String.valueOf(limit));
        } 
        return algorithmParams;
    }  
}
