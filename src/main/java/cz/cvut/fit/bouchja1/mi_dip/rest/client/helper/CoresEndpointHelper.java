/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.helper;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.UserArticleDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.CoreSolrService;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.SolrService;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.validation.UserArticleValidator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

/**
 *
 * @author jan
 */
@Service
public class CoresEndpointHelper extends CommonEndpointHelper {

    //@Autowired
    private CoreSolrService coreSolrService;
    private final Log logger = LogFactory.getLog(getClass());

    public Response putUserArticle(String coreId, UserArticleDocument userArticle) {
        Response resp = null;
        if (coreSolrService.getSolrService().isServerCoreFromPool(coreId)) {
            String message = UserArticleValidator.validateUserArticle(userArticle);
            if ("success".equals(message)) {
                try {
                    coreSolrService.putUserArticle(coreId, userArticle);
                    resp = getOkResponse();
                } catch (SolrServerException ex) {
                    logger.error(ex);
                    resp = getServerError(ex.getMessage());
                } catch (IOException ex) {
                    logger.error(ex);
                    resp = getServerError(ex.getMessage());
                }
            } else {
                resp = getBadRequestResponse(message);
            }
        } else {
            //vratit odpoved, ze takovy core-id tam neexistuje
            resp = getBadRequestResponse("You filled bad or non-existing {core-id}.");
        }

        return resp;
    }

    public Response putUserArticle(String coreId, MultivaluedMap queryParams) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Response disableArticle(String coreId, String documentId) {
        Response resp = null;
        if (coreSolrService.getSolrService().isServerCoreFromPool(coreId)) {
            if (documentId != null) {
                try {
                coreSolrService.disableArticle(coreId, documentId);
                resp = getOkResponse();
                } catch (SolrServerException ex) {
                    logger.error(ex);
                    resp = getServerError(ex.getMessage());
                } catch (WebApplicationException ex) {
                    logger.error(ex);
                    resp = getNotFoundResponse(ex.getMessage());
                } catch (IOException ex) {
                    logger.error(ex);
                    resp = getServerError(ex.getMessage());                    
                }      
            } else {
                resp = getBadRequestResponse("You need to specify documentId in query param.");
            }
        } else {
            //vratit odpoved, ze takovy core-id tam neexistuje
            resp = getBadRequestResponse("You filled bad or non-existing {core-id}.");
        }

        return resp;
    }

    public void setCoreSolrService(CoreSolrService coreSolrService) {
        this.coreSolrService = coreSolrService;
    }


}
