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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author jan
 */
public class AlgorithmItemBasedCf implements IAlgorithm {

    private static final String ALGORITHM_NAME = "itemcf";
    private String id;
    
    private final Log logger = LogFactory.getLog(getClass());
    private ConcurrentUpdateSolrServer server;
    private String coreId;
    private String groupId;
    private String articleId;
    private String limit;  
    private String userId;
    
    private long articleIdInt;

    public AlgorithmItemBasedCf(Map<String, String> algorithmParams) {
        this.coreId = algorithmParams.get("coreId");
        this.groupId = algorithmParams.get("groupId");
        this.articleId = algorithmParams.get("articleId");
        this.limit = algorithmParams.get("limit");
        this.userId = algorithmParams.get("userId");
        this.id = ALGORITHM_NAME;
    }

    @Override
    public Response recommend(SolrService solrService, AlgorithmEndpointHelper helper) {
        Response resp;
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        if (solrService.isServerCoreFromPool(coreId)) {
            int limitToQuery = Util.getCountOfElementsToBeReturned(limit);
            try {
                docs = getRecommendationByItemBasedCf(coreId, articleId, groupId, limitToQuery, solrService);
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

    private List<OutputDocument> getRecommendationByItemBasedCf(String coreId, String articleId, String groupId, int limit, SolrService solrService) throws SolrServerException {
        this.server = solrService.getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        List<SolrDocument> docsToReturn = new ArrayList<SolrDocument>();

        List<Object> collaborativeUsers = new ArrayList<Object>();

        try {
            SolrQuery query = new SolrQuery();

            query.setQuery("articleId:(\"" + articleId + "\")");

            //STEP 1 - Find similar users who like the same document
            QueryResponse response = server.query(query);
            SolrDocumentList docsList = response.getResults();

            // vrati mi to vsechny uzivatele, ktery maji radi ten dokument
            if (!docsList.isEmpty()) {
                collaborativeUsers = findDocUsers(docsList);
            }

            //STEP 2 - Search for docs "liked" by those similar users
            FastByIDMap<FastIDSet> userData = new FastByIDMap<FastIDSet>();

            fillUserData(response, collaborativeUsers, userData);

            DataModel model = new GenericBooleanPrefDataModel(userData);
            ItemSimilarity itemSimilarity = new LogLikelihoodSimilarity(model);
            ItemBasedRecommender recommender = new GenericItemBasedRecommender(model, itemSimilarity);

            List<RecommendedItem> recommendedItems = recommender.recommend(articleIdInt, limit);

            processRecommendedItems(response, recommendedItems, docsToReturn);

        } catch (Exception e) {
            e.printStackTrace();
            //return Response.status(500).entity("error : " + e.toString()).build();
        }
        
        return docs;  
    }
    
    private List<Object> findDocUsers(SolrDocumentList docs) {
        List<Object> collaborativeUsersList = null;
        Iterator<SolrDocument> docsIterator = docs.iterator();

        while (docsIterator.hasNext()) {
            SolrDocument doc = docsIterator.next();
            Collection<Object> usersInDoc = doc.getFieldValues("userId");
            collaborativeUsersList = Lists.newArrayList(usersInDoc);
            articleIdInt = Long.parseLong(doc.getFieldValue("id") + "");
        }

        return collaborativeUsersList;
    }    
    
    private void fillUserData(QueryResponse response, List<Object> collaborativeUsers, FastByIDMap<FastIDSet> userData) throws SolrServerException {
        SolrQuery cfQuery = new SolrQuery();
        cfQuery.setFields("id");

        if (!collaborativeUsers.isEmpty()) {
            for (int i = 0; i < collaborativeUsers.size(); i++) {
                cfQuery.setQuery("userId:" + collaborativeUsers.get(i).toString());
                response = server.query(cfQuery);
                SolrDocumentList results = response.getResults();

                long[] itemValues = new long[results.size()];

                for (int j = 0; j < results.size(); j++) {
                    SolrDocument d = results.get(j);
                    itemValues[j] = Long.parseLong(d.getFieldValue("id") + "");
                }
                userData.put(Long.parseLong(collaborativeUsers.get(i) + ""), new FastIDSet(itemValues));
            }
        }
    }

    private void processRecommendedItems(QueryResponse response, List<RecommendedItem> recommendedItems, List<SolrDocument> docsToReturn) throws SolrServerException {
        System.out.println("Recommended items for articleId# " + articleId);

        SolrQuery queryReturnArticles = new SolrQuery();

        for (RecommendedItem recommendedItem : recommendedItems) {
            System.out.println(recommendedItem.getItemID());
            queryReturnArticles.setQuery("id:" + recommendedItem.getItemID());
            response = server.query(queryReturnArticles);
            SolrDocumentList results = response.getResults();
            for (int i = 0; i < results.size(); i++) {
                System.out.println(results.get(i));
                docsToReturn.add(results.get(i));
            }
        }
    }    

    public String getId() {
        return id;
    }
    
    
}
