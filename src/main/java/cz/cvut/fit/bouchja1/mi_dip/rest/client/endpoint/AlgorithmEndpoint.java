/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public interface AlgorithmEndpoint {
    public Response recommend(String algorithmId,
            String coreId,
            int groupId,
            int userId,
            String documentId,
            String text,
            int limit);  
    public Response getSupportedAlgorithms();
}
