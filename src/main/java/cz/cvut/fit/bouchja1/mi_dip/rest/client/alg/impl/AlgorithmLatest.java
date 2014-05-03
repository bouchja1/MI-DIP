/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.impl;

import com.google.common.collect.Lists;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.IAlgorithm;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.OutputDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.AlgorithmEndpointHelper;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.SolrService;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author jan
 */
public class AlgorithmLatest implements IAlgorithm {
    
    private static final String ALGORITHM_NAME = "latest";
    
    private final Log logger = LogFactory.getLog(getClass());
    
    private String coreId;
    private String groupId;
    private String limit;
        
    private String id;
    
    public AlgorithmLatest(Map<String, String> algorithmParams) {
        this.coreId = algorithmParams.get("coreId");
        this.groupId = algorithmParams.get("groupId");
        this.limit = algorithmParams.get("limit");
        this.id = ALGORITHM_NAME;
    }    

//curl -i -H "Accept: application/json" -H "Content-Type: application/json" 'http://localhost:8089/ensembleRestApi/recommeng/algorithm/userBased/latest?groupId=11&limit=5'        
    @Override
    public Response recommend(SolrService solrService, AlgorithmEndpointHelper helper) {
        Response resp;
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        if (solrService.isServerCoreFromPool(coreId)) {
            int limitToQuery = Util.getCountOfElementsToBeReturned(limit);
            try {
                docs = getRecommendationByLatest(coreId, groupId, limitToQuery, solrService);
                resp = Response.ok(
                        new GenericEntity<List<OutputDocument>>(Lists.newArrayList(docs)) {
                }).build();
            } catch (SolrServerException ex) {
                logger.error(ex);
                resp = helper.getServerError(ex.getMessage());
            }
        } else {
            //vratit odpoved, ze takovy core-id tam neexistuje
            resp = helper.getBadRequestResponse("You filled bad or non-existing {core-id}.");
        }
        return resp;
    }
    
    private List<OutputDocument> getRecommendationByLatest(String coreId, String groupId, int limitToQuery, SolrService solrService) throws SolrServerException {
        HttpSolrServer server = solrService.getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();

        String sortOrder = "time";
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        String groupIdString;
        if (groupId != null) {
            groupIdString = groupId;
        } else {
            groupIdString = "*";
        }
        query.setFilterQueries("usedInRec:true", "group:" + groupIdString);
        query.setRows(limitToQuery);
        query.setSortField(sortOrder, SolrQuery.ORDER.desc);

        QueryResponse response;
        response = server.query(query);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            System.out.println(results.get(i));
            OutputDocument output = Util.fillOutputDocument(results.get(i));
            docs.add(output);
        }
        solrService.incrementImpression(coreId, results);

        return docs;
    }    

    public String getId() {
        return id;
    }
    
    
}
