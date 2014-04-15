/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.helper;

import com.google.common.collect.Lists;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.OutputDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.AlgorithmSolrService;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.util.Util;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;

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

//curl -i -H "Accept: application/json" -H "Content-Type: application/json" 'http://localhost:8089/ensembleRestApi/recommeng/algorithm/userBased/random?limit=5'
    public Response getRecommendationByRandom(String coreId, int limit) {    
        Response resp = null;
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        if (algorithmSolrService.getSolrService().isServerCoreFromPool(coreId)) {
            int limitToQuery = Util.getCountOfElementsToBeReturned(limit);
            try {
                docs = algorithmSolrService.getRecommendationByRandom(coreId, limitToQuery);
                resp = Response.ok(
                        new GenericEntity<List<OutputDocument>>(Lists.newArrayList(docs)) {
                }).build();
            } catch (SolrServerException ex) {
                logger.error(ex);
                resp = getServerError(ex.getMessage());
            }
        } else {
            //vratit odpoved, ze takovy core-id tam neexistuje
            resp = getBadRequestResponse("You filled bad or non-existing {core-id}.");
        }
        return resp;
    }
}