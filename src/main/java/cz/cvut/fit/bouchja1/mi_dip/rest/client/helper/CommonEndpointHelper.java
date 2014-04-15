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
}
