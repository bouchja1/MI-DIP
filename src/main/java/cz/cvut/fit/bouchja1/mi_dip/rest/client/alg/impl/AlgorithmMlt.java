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
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MoreLikeThisParams;

/**
 *
 * @author jan
 */
public class AlgorithmMlt implements IAlgorithm {
    
    private static final String ALGORITHM_NAME = "mlt";
    private String id;
    
    private final Log logger = LogFactory.getLog(getClass());
    
    private String coreId;
    private String limit;
    private String documentId;
        
    public AlgorithmMlt(Map<String, String> algorithmParams) {
        this.coreId = algorithmParams.get("coreId");
        this.limit = algorithmParams.get("limit");
        this.documentId = algorithmParams.get("documentId");
        this.id = ALGORITHM_NAME;
    }     

    /*
     * curl -i -H "Accept: application/json" -H "Content-Type: application/json" 'http://localhost:8089/ensembleRestApi/recommeng/algorithm/userBased/morelikethisid?limit=5'        
     */
    @Override
    public Response recommend(SolrService solrService, AlgorithmEndpointHelper helper) {
        Response resp;
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        if (solrService.isServerCoreFromPool(coreId)) {
            try {
                int limitToQuery = Util.getCountOfElementsToBeReturned(limit);
                SolrDocument existingDocument = solrService.isDocumentInIndex(coreId, documentId);
                if (existingDocument != null) {
                    docs = getRecommendationByMltId(coreId, existingDocument, limitToQuery, solrService);
                    resp = Response.ok(
                            new GenericEntity<List<OutputDocument>>(Lists.newArrayList(docs)) {
                    }).build();
                } else {
                    resp = helper.getBadRequestResponse("Document with id " + documentId + " was not found in index.");
                }
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
    
    /*
     * Vyznamy jednotlivych parametru zde: https://wiki.apache.org/solr/MoreLikeThis
     */
    private List<OutputDocument> getRecommendationByMltId(String coreId, SolrDocument document, int limitToQuery, SolrService solrService) throws SolrServerException {
        HttpSolrServer server = solrService.getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();

        SolrQuery query = new SolrQuery();
        query.setRequestHandler("/" + MoreLikeThisParams.MLT);
        query.set(MoreLikeThisParams.MATCH_INCLUDE, true);
        query.set(MoreLikeThisParams.MIN_DOC_FREQ, 1);
        query.set(MoreLikeThisParams.MIN_TERM_FREQ, 1);
        query.set(MoreLikeThisParams.MIN_WORD_LEN, 1);
        query.set(MoreLikeThisParams.BOOST, false);
        query.set(MoreLikeThisParams.SIMILARITY_FIELDS, "articleText");
        query.set(MoreLikeThisParams.MAX_QUERY_TERMS, 1000);
        query.setRows(limitToQuery);
        query.setQuery("articleId:" + document.getFieldValue("articleId"));
        query.setFilterQueries("usedInRec:true", "group:" + document.getFieldValue("group"));

        QueryResponse response = server.query(query);
        SolrDocumentList results = response.getResults();

        System.out.println(results.getNumFound() + " documents found by more like this.");
        for (int i = 0; i < results.size(); ++i) {
            System.out.println(results.get(i).get("id") + "," + results.get(i).get("articleText"));
            OutputDocument output = Util.fillOutputDocument(results.get(i));
            docs.add(output);
        }

        return docs;
    }    
    
}
