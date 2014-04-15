/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.helper;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.UserArticle;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.SolrService;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jan
 */
@Service
public class CoresEndpointHelper extends CommonEndpointHelper {
    
    //@Autowired
    private SolrService solrService;

    public Response putUserArticle(String coreId, UserArticle userArticle) {
        //VALIDACE, zda byly dobre zadany parametry (coreId) nebo zda je ten json ok a tak
        System.out.println("sfddfs");
        //zjisteni zda uz to tam je ci ne
        
        //ulozeni do solr
        //Article a = 
        
        //vraceni response
        //return getSeeOtherResponse();
        return null;
    }

    public Response putUserArticle(String coreId, MultivaluedMap queryParams) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    

    public Response disableArticle(String coreId, String documentId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setSolrService(SolrService solrService) {
        this.solrService = solrService;
    }
    
    
}
