/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.solr;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.OutputDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.UserIdDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.util.Util;
import ec.util.MersenneTwisterFast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.apache.solr.schema.DateField;
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
    private static final String DOC_ID = "id";
    private static final String ARTICLE_ID = "articleId";
    private static final String ARTICLE_TEXT = "articleText";
    private static final String USER_ID = "userId";
    private static final String GROUP = "group";
    private static final String TIME = "time";
    private static final String RECCOMM_FLAG = "usedInRecommendation";
    private static final String characters = "abcdefghijklmnopqrstuvwxyz 0123456789";
    private Random rng = new Random();

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
    public List<OutputDocument> getRecommendationByMltId(String coreId, SolrDocument document, int limitToQuery) throws SolrServerException {
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
        query.setFilterQueries("usedInRecommendation:true", "group:" + document.getFieldValue("group"));

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

    public List<OutputDocument> getRecommendationByMltText(String coreId, String text, int limitToQuery) throws SolrServerException, IOException {
        HttpSolrServer server = solrService.getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(DOC_ID, SolrAutoIncrementer.getLastIdToUse(server));
        String generatedArticleId = generateArticleId();
        doc.addField(ARTICLE_ID, generatedArticleId);
        doc.addField(ARTICLE_TEXT, text);
        doc.addField(GROUP, 0);
        doc.addField(RECCOMM_FLAG, true);
        doc.addField(TIME, DateField.formatExternal(new Date()));
        server.add(doc);
        UpdateResponse commit = server.commit();   

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
        query.setQuery("articleId:" + generatedArticleId);
        query.setFilterQueries("usedInRecommendation:true");

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

    private String generateArticleId() {
        char[] id = new char[50];
        for (int i = 0; i < 50; i++) {
            id[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(id);
    }
}
