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
public interface EndpointHelper {

    public Response getOkResponse();

    public Response getOkResponse(String message);
    
    public Response getBadRequestResponse(String message);
    
    public Response getNotFoundResponse(String message);

    public Response getCreatedResponse(URI uri);

    public Response getSeeOtherResponse(URI uri);
    
    public Response getServerError(String message);

    public Response build(ResponseBuilder builder);
    
    public Response build(ResponseBuilder builder, String message);
}
