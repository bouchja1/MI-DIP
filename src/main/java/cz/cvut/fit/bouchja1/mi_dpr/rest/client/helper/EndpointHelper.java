/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dpr.rest.client.helper;

import java.net.URI;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 *
 * @author jan
 */
public interface EndpointHelper<T> {

    public Response getOkResponse();

    public Response getOkResponse(Object o);

    public Response getCreatedResponse(URI uri);

    public Response getSeeOtherResponse(URI uri);

    public Response build(ResponseBuilder builder);
}
