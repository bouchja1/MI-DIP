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
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author jan
 */
public class AlgorithmWeightedRating implements IAlgorithm {
    
    private static final String ALGORITHM_NAME = "toprate";
    private String id;
    
    private String coreId;
    private String groupId;
    private String limit;       
    
    private final Log logger = LogFactory.getLog(getClass());
    
    public AlgorithmWeightedRating(Map<String, String> algorithmParams) {
        this.coreId = algorithmParams.get("coreId");
        this.groupId = algorithmParams.get("groupId");
        this.limit = algorithmParams.get("limit");
        this.id = ALGORITHM_NAME;
    }        

    //Based on sources:
    //http://stackoverflow.com/questions/2134504/what-is-the-best-algorithm-to-calculate-the-most-scored-item
    //http://www.imdb.com/chart/top
    //http://en.wikipedia.org/wiki/Internet_Movie_Database#User_ratings_of_films
    
    @Override
    public Response recommend(SolrService solrService, AlgorithmEndpointHelper helper) {
        Response resp;
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        if (solrService.isServerCoreFromPool(coreId)) {
            int limitToQuery = Util.getCountOfElementsToBeReturned(limit);
            try {
                docs = getRecommendationByTopRated(coreId, groupId, limitToQuery, solrService);
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

    private List<OutputDocument> getRecommendationByTopRated(String coreId, String groupId, int limitToQuery, SolrService solrService) throws SolrServerException {
        ConcurrentUpdateSolrServer server = solrService.getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        
        String sortOrder = "weightedRating";
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        String groupIdString;
        if (groupId != null) {
            groupIdString = groupId;
        } else {
            groupIdString = "*";
        }
        query.setFilterQueries("group:" + groupIdString);
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
        //solrService.incrementImpression(coreId, results);

        return docs;       
    }    
    
    public String getId() {
        return id;
    }   
    
}
