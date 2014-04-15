/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.zeromq.ZeroMqClientHelper;
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

    @Autowired
    private ZeroMqClientHelper zmqClientHelper;

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.TEXT_PLAIN)        
    public Response createBanditCollection(String banditSetId, List<String> banditIds) {
        String message = zmqClientHelper.createBanditSet();
        return null;
    }

    public Response detectBestBandit(String banditCollectionId) {
        String message = zmqClientHelper.detectBestBandit();
        return null;
    }
    
    public Response sendBanditPull() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Response sendFeedback() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @GET
    @Path("/{parameter}")
    public Response responseMsg(@PathParam("parameter") String parameter,
            @DefaultValue("Nothing to say") @QueryParam("value") String value) {
        String neco = zmqClientHelper.getNeco();
        String output = "Hello from: " + parameter + " : " + value + "LOOOL:  " + neco;
        return Response.status(200).entity(output).build();
    }    
}
