/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.helper;

import com.google.common.collect.Lists;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.IAlgorithm;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.OutputDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.AlgorithmSolrService;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;

/**
 *
 * @author jan
 */
public class AlgorithmEndpointHelper extends CommonEndpointHelper {

    private AlgorithmSolrService algorithmSolrService;
    private final Log logger = LogFactory.getLog(getClass());

    public void setAlgorithmSolrService(AlgorithmSolrService algorithmSolrService) {
        this.algorithmSolrService = algorithmSolrService;
    }

    /*

    public Response getRecommendationByMltText(String coreId, String text, int limit) {
        Response resp = null;
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        if (algorithmSolrService.getSolrService().isServerCoreFromPool(coreId)) {
            int limitToQuery = Util.getCountOfElementsToBeReturned(limit);
            try {
                docs = algorithmSolrService.getRecommendationByMltText(coreId, text, limitToQuery);
                resp = Response.ok(
                        new GenericEntity<List<OutputDocument>>(Lists.newArrayList(docs)) {
                }).build();
            } catch (SolrServerException ex) {
                logger.error(ex);
                resp = getServerError(ex.getMessage());
            } catch (IOException ex) {
                logger.error(ex);
                resp = getServerError(ex.getMessage());
            }
        } else {
            //vratit odpoved, ze takovy core-id tam neexistuje
            resp = getBadRequestResponse("You filled bad or non-existing {core-id}.");
        }

        return resp;
    }
*/
    public Response getRecommendation(IAlgorithm algorithm) {
        return algorithm.recommend(algorithmSolrService, this);
    }
    
    public Response createAlgorithmNotFound() {
        return getNotFoundResponse("The resource with algorithm you request for does not exist.");
    }    
}
