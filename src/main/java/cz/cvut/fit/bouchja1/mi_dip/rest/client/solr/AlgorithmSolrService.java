/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.solr;

import java.util.Random;
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

    /*
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
    * */

    private String generateArticleId() {
        char[] id = new char[50];
        for (int i = 0; i < 50; i++) {
            id[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(id);
    }
}
