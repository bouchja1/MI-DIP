/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.helper;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.IAlgorithm;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.SolrService;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jan
 */
public class AlgorithmEndpointHelper extends CommonEndpointHelper {

    private SolrService solrService;
    private final Log logger = LogFactory.getLog(getClass());

    public void setSolrService(SolrService solrService) {
        this.solrService = solrService;
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
        return algorithm.recommend(solrService, this);
    }
    
    public Response createAlgorithmNotFound() {
        return getNotFoundResponse("The algorithm you request for does not exist (it is not supported algorithm or you entered bad name).");
    }    
}
