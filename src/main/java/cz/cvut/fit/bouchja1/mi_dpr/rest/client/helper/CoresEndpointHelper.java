/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dpr.rest.client.helper;

import cz.cvut.fit.bouchja1.mi_dpr.rest.client.domain.UserArticle;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.springframework.stereotype.Service;

/**
 *
 * @author jan
 */
@Service
public class CoresEndpointHelper {

    public Response putUserArticle(String coreId, UserArticle userArticle) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Response putUserArticle(String coreId, MultivaluedMap queryParams) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    

    public Response disableArticle(String coreId, String documentId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
