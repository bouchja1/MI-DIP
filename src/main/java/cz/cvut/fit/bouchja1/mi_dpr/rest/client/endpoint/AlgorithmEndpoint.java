/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dpr.rest.client.endpoint;

import javax.ws.rs.Path;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 */
@Component
@Path(AlgorithmEndpoint.ENDPOINT_PATH)
public class AlgorithmEndpoint {
    
    public static final String ENDPOINT_PATH = "/algorithm";
    
}
