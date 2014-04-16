/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.solr;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.OutputDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.UserIdDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.util.Util;
import ec.util.MersenneTwisterFast;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 */
@Component
@Scope("singleton")
public class AlgorithmSolrService {

    @Autowired
    private SolrService solrService;
    private MersenneTwisterFast generator = new MersenneTwisterFast();

    public SolrService getSolrService() {
        return solrService;
    }

    public List<OutputDocument> getRecommendationByRandom(String coreId, int groupId, int limit) throws SolrServerException {
        HttpSolrServer server = solrService.getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        int random = generator.nextInt(Integer.MAX_VALUE) + 1; // values are between 1 and Integer.MAX_VALUE
        String sortOrder = "random_" + random;
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        String groupIdString;
        if (groupId > 0) {
            groupIdString = String.valueOf(groupId);
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

    public List<OutputDocument> getRecommendationByLatest(String coreId, int groupId, int limitToQuery) throws SolrServerException {
        HttpSolrServer server = solrService.getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();

        String sortOrder = "time";
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        String groupIdString;
        if (groupId > 0) {
            groupIdString = String.valueOf(groupId);
        } else {
            groupIdString = "*";
        }
        query.setFilterQueries("usedInRecommendation:true", "group:" + groupIdString);
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

        return docs;
    }

    /*
     * Vyznamy jednotlivych parametru zde: https://wiki.apache.org/solr/MoreLikeThis
     */
    public List<OutputDocument> getRecommendationByMltId(String coreId, String documentId, int limitToQuery) throws SolrServerException {
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
        query.setQuery("articleId:" + documentId);

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

    public List<OutputDocument> getRecommendationByMltText(String coreId, String text, int limitToQuery) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
