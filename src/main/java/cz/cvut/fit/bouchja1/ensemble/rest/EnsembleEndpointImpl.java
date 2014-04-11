/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.rest;

import cz.cvut.fit.bouchja1.ensemble.zeromq.ZeroMqClientHelper;
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
@Path("/ensemble")
public class EnsembleEndpointImpl implements EnsembleEndpoint {

    @Autowired
    private ZeroMqClientHelper zmqClientHelper;

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.TEXT_PLAIN)    
    @Override
    public Response createBanditSet(String banditSetId, List<String> banditIds) {
        String message = zmqClientHelper.createBanditSet();
        return null;
    }

    @Override
    public Response detectBestBandit(String banditCollectionId) {
        String message = zmqClientHelper.detectBestBandit();
        return null;
    }

    @Override
    public Response requestBanditRecommendation(String banditCollectionId, String banditId) {
        String message = zmqClientHelper.requestBanditRecommendation();
        return null;
    }

    @GET
    @Path("/{parameter}")
    @Override
    public Response responseMsg(@PathParam("parameter") String parameter,
            @DefaultValue("Nothing to say") @QueryParam("value") String value) {
        String neco = zmqClientHelper.getNeco();
        String output = "Hello from: " + parameter + " : " + value + "LOOOL:  " + neco;
        return Response.status(200).entity(output).build();
    }
}
