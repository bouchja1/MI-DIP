/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.helper;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.CreatedCollection;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.Message;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author jan
 */
public abstract class CommonEndpointHelper implements EndpointHelper {

    @Override
    public Response getOkResponse() {
        return build(Response.ok());
    }

    @Override
    public Response getOkResponse(String message) {
        return build(Response.status(Response.Status.OK), message);
    }
    
    @Override
    public Response getNotFoundResponse(String message) {
        return build(Response.status(Response.Status.NOT_FOUND), message);
    }    

    @Override
    public Response getCreatedResponse(String identifier, int id) {
        URI uri = null; 
        try {
            uri = new URI("/" + identifier + "/" + id);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return build(Response.created(uri));
    }

    @Override
    public Response getSeeOtherResponse(URI uri) {
        return build(Response.seeOther(uri));
    }

    @Override
    public Response build(ResponseBuilder builder) {
        return builder.build();
    }
    
    @Override
    public Response build(ResponseBuilder builder, String message) {
        return builder.entity(new Message(message)).build();
    }    

    @Override
    public Response getBadRequestResponse(String message) {
        return build(Response.status(Response.Status.BAD_REQUEST), message);     
    }

    @Override
    public Response getServerError(String message) {
        return build(Response.status(Response.Status.INTERNAL_SERVER_ERROR), message);
    }
    
    @Override
    public Response getOkResponseEnsemble(String result) {
        return buildEnsemble(Response.status(Response.Status.OK), result);
    }

    @Override
    public Response getCreatedResponseEnsemble(String result) {
            //uri = new URI("/" + identifier + "/" + id);
               
        return buildEnsemble(Response.status(Response.Status.CREATED), result);
    }

    @Override
    public Response getBadRequestResponseEnsemble(String result) {
        return buildEnsemble(Response.status(Response.Status.BAD_REQUEST), result); 
    }

    @Override
    public Response getNotFoundResponseEnsemble(String result) {
        return buildEnsemble(Response.status(Response.Status.NOT_FOUND), result);
    }

    @Override
    public Response getServerErrorEnsemble(String result) {
        return buildEnsemble(Response.status(Response.Status.INTERNAL_SERVER_ERROR), result);
    }    
    
    @Override
    public Response buildEnsemble(URI uri) { 
        return Response.status(201).entity(new CreatedCollection(uri.toString())).build();
    }
    
    @Override
    public Response buildEnsemble(ResponseBuilder builder, String message) {
        return builder.entity(message).build();
    }        
}