/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.impl;

import com.google.common.collect.Lists;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.IAlgorithm;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.OutputDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.AlgorithmEndpointHelper;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.AlgorithmSolrService;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.util.Util;
import ec.util.MersenneTwisterFast;
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
public class AlgorithmRandom implements IAlgorithm {
    private final Log logger = LogFactory.getLog(getClass());
    
    private static final String ALGORITHM_NAME = "random";
    
    private MersenneTwisterFast generator = new MersenneTwisterFast();
    
    private String coreId;
    private String groupId;
    private String limit;
    
    private String id;
        
    public AlgorithmRandom(Map<String, String> algorithmParams) {
        this.coreId = algorithmParams.get("coreId");
        this.groupId = algorithmParams.get("groupId");
        this.limit = algorithmParams.get("limit");
        this.id = ALGORITHM_NAME;
    }

    /*
     * //curl -i -H "Accept: application/json" -H "Content-Type: application/json" 'http://localhost:8089/ensembleRestApi/recommeng/algorithm/userBased/random?limit=5'
     */
    @Override
    public Response recommend(AlgorithmSolrService algorithmSolrService, AlgorithmEndpointHelper helper) {
        Response resp;
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        if (algorithmSolrService.getSolrService().isServerCoreFromPool(coreId)) {
            int limitToQuery = Util.getCountOfElementsToBeReturned(limit);
            try {
                docs = getRecommendationByRandom(coreId, groupId, limitToQuery, algorithmSolrService);
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
    
    private List<OutputDocument> getRecommendationByRandom(String coreId, String groupId, int limit, AlgorithmSolrService algorithmSolrService) throws SolrServerException {
        HttpSolrServer server = algorithmSolrService.getSolrService().getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        int random = generator.nextInt(Integer.MAX_VALUE) + 1; // values are between 1 and Integer.MAX_VALUE
        String sortOrder = "random_" + random;
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        String groupIdString;
        if (groupId != null) {
            groupIdString = groupId;
        } else {
            groupIdString = "*";
        }
        query.setFilterQueries("usedInRecommendation:true", "group:" + groupIdString);
        query.setRows(limit);
        query.setSortField(sortOrder, SolrQuery.ORDER.desc);

        QueryResponse response;
        response = server.query(query);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            System.out.println(results.get(i));
            OutputDocument output = Util.fillOutputDocument(results.get(i));
            docs.add(output);
        }
        return docs;
    }    

    public String getId() {
        return id;
    }
    
    
    
}
