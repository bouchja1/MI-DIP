package cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.impl;

import com.google.common.collect.Lists;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.IAlgorithm;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.OutputDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.AlgorithmEndpointHelper;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.SolrService;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.util.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * 
 * http://kickstarthadoop.blogspot.cz/2011/05/generating-recommendations-with-mahout_26.html
 */
public class AlgorithmItemBasedCf implements IAlgorithm {

    private static final String ALGORITHM_NAME = "cfitem";
    private String id;
    private final Log logger = LogFactory.getLog(getClass());
    private ConcurrentUpdateSolrServer server;
    private String coreId;
    private String groupId;
    //private String articleId;
    private String limit;
    private String userId;
    private long articleIdInt;

    public AlgorithmItemBasedCf(Map<String, String> algorithmParams) {
        this.coreId = algorithmParams.get("coreId");
        this.groupId = algorithmParams.get("groupId");
        //this.articleId = algorithmParams.get("articleId");
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
                docs = getRecommendationByItemBasedCf(coreId, userId, groupId, limitToQuery, solrService);
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

    private List<OutputDocument> getRecommendationByItemBasedCf(String coreId, String userId, String groupId, int limit, SolrService solrService) throws SolrServerException {
        this.server = solrService.getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();

        /*
         * We need userIds and their articles - userid field represents the unique
         * user identification and articleList is a list of article ids. 
         * So if we execute a Solr query then we get a response like this
         */
        Set<Integer> userIdsSet = new HashSet<Integer>();

        SolrQuery testQuery = new SolrQuery();
        // Potrebuju projet vsechny dokumenty v indexu a od kazdyho ulozit do Set userId
        testQuery.setQuery("id:*");

        String groupIdString;
        if (groupId != null) {
            groupIdString = groupId;
        } else {
            groupIdString = "*";
        }
        testQuery.setFilterQueries("group:" + groupIdString);

        testQuery.setRows(0);
        QueryResponse response = server.query(testQuery);

        fillUserIdsSet(response, userIdsSet);

        //nyni mam tedy userIds a mohu se dotazovat a ziskavat jejich articles

        //Mahout Map and Set implementations
        FastByIDMap<FastIDSet> userData = new FastByIDMap<FastIDSet>();

        createUserData(response, userIdsSet, userData, groupId);

        DataModel model = new GenericBooleanPrefDataModel(userData);

        try {

            ItemSimilarity itemSimilarity = new LogLikelihoodSimilarity(model);
            ItemBasedRecommender recommender = new GenericItemBasedRecommender(model, itemSimilarity);

            List<RecommendedItem> recommendedItems = recommender.recommend(Long.parseLong(userId), limit);

            List<SolrDocument> docsToReturn = processRecommendedItems(response, recommendedItems);

            for (SolrDocument d : docsToReturn) {
                OutputDocument output = Util.fillOutputDocument(d);
                docs.add(output);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //return Response.status(500).entity("error : " + e.toString()).build();
        }

        return docs;
    }

    private void createUserData(QueryResponse response, Set<Integer> userIdsSet, FastByIDMap<FastIDSet> userData, String groupId) throws SolrServerException {
        SolrQuery cfQuery = new SolrQuery();
        cfQuery.setRows(Integer.MAX_VALUE);
        cfQuery.setFields("id");
        String groupIdString;
        if (groupId != null) {
            groupIdString = groupId;
        } else {
            groupIdString = "*";
        }
        cfQuery.setFilterQueries("group:" + groupIdString);
        //nyni ziskej ty dvojice uzivatelId - articleId
        Iterator<Integer> userSetIterator = userIdsSet.iterator();
        while (userSetIterator.hasNext()) {
            Long userRelatedId = userSetIterator.next().longValue();
            cfQuery.setQuery("userId:" + userRelatedId);
            response = server.query(cfQuery);
            SolrDocumentList results = response.getResults();
            long[] itemValues = new long[results.size()];
            //List<Integer> itemValues = new ArrayList<Integer>();

            for (int j = 0; j < results.size(); j++) {
                SolrDocument d = results.get(j);
                //itemValues.add((Integer)d.getFieldValue("id"));
                itemValues[j] = Long.parseLong(d.getFieldValue("id") + "");
            }
            userData.put(userRelatedId, new FastIDSet(itemValues));
        }
    }

    private void fillUserIdsSet(QueryResponse response, Set<Integer> userIdsSet) throws SolrServerException {
        int numFound = (int) response.getResults().getNumFound();

        SolrQuery query = new SolrQuery();
        query.setQuery("id:*");
        String groupIdString;
        if (groupId != null) {
            groupIdString = groupId;
        } else {
            groupIdString = "*";
        }
        query.setFilterQueries("group:" + groupIdString);
        //query.setRows(limitToQuery);           
        query.setFields("userId");

        for (int i = 0; i < numFound; i = i + 50) {
            query.setStart(i);
            query.setRows(50);
            response = server.query(query);
            SolrDocumentList results = response.getResults();
            for (int j = 0; j < results.size(); j++) {
                Collection<Object> userIdsInDoc = results.get(j).getFieldValues("userId");
                if (userIdsInDoc != null) {
                    Iterator<Object> userIdsInDocIterator = userIdsInDoc.iterator();
                    while (userIdsInDocIterator.hasNext()) {
                        Integer tempUserId = (Integer) userIdsInDocIterator.next();
                        userIdsSet.add(tempUserId);
                    }
                }
            }
        }
    }

    private List<SolrDocument> processRecommendedItems(QueryResponse response, List<RecommendedItem> recommendedItems) throws SolrServerException {
        System.out.println("Recommended items for userId# " + userId);
        List<SolrDocument> docsToReturn = new ArrayList<SolrDocument>();
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

        return docsToReturn;
    }

    public String getId() {
        return id;
    }
}
