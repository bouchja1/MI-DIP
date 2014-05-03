/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.helper;

import java.net.URI;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

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
    public Response getOkResponse(Object o) {
        return build(Response.ok(o));
    }
    
    @Override
    public Response getNotFoundResponse(String message) {
        return build(Response.status(Response.Status.NOT_FOUND), message);
    }    

    @Override
    public Response getCreatedResponse(URI uri) {
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
        return builder.entity(message).build();
    }    

    @Override
    public Response getBadRequestResponse(String message) {
        return build(Response.status(Response.Status.BAD_REQUEST), message);
    }

    @Override
    public Response getServerError(String message) {
        return build(Response.status(Response.Status.INTERNAL_SERVER_ERROR), message);
    }
}