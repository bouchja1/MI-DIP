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
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author jan
 */
public class AlgorithmUserBasedCf implements IAlgorithm {
    private final Log logger = LogFactory.getLog(getClass());
    
    private static final String ALGORITHM_NAME = "usercf";
    private String id;

    private HttpSolrServer server;
    
    private String coreId;
    private String groupId;
    private String userId;
    private String limit;
        
    public AlgorithmUserBasedCf(Map<String, String> algorithmParams) {
        this.coreId = algorithmParams.get("coreId");
        this.groupId = algorithmParams.get("groupId");
        this.userId = algorithmParams.get("userId");
        this.limit = algorithmParams.get("limit");
        this.id = ALGORITHM_NAME;
    }    

    @Override
    public Response recommend(SolrService solrService, AlgorithmEndpointHelper helper) {
        Response resp;
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        if (solrService.isServerCoreFromPool(coreId)) {
            int limitToQuery = Util.getCountOfElementsToBeReturned(limit);
            try {
                docs = getRecommendationByUserBasedCf(coreId, userId, groupId, limitToQuery, solrService);
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
    
    private List<OutputDocument> getRecommendationByUserBasedCf(String coreId, String userId, String groupId, int limit, SolrService solrService) throws SolrServerException {
        this.server = solrService.getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        
        /*
         * We need userIds and their articles - userid field represents the unique
         * user identification and articleList is a list of article ids. 
         * So if we execute a Solr query then we get a response like this
         */
        Set<Integer> userIdsSet = new HashSet<Integer>();
        List<SolrDocument> docsToReturn = new ArrayList<SolrDocument>();

        try {
            SolrQuery testQuery = new SolrQuery();
            // Potrebuju projet vsechny dokumenty v indexu a od kazdyho ulozit do Set userId
            testQuery.setQuery("id:*");
            testQuery.setRows(0);
            QueryResponse response = server.query(testQuery);

            fillUserIdsSet(response, userIdsSet);

            //nyni mam tedy userIds a mohu se dotazovat a ziskavat jejich articles

            //Mahout Map and Set implementations
            FastByIDMap<FastIDSet> userData = new FastByIDMap<FastIDSet>();

            createUserData(response, userIdsSet, userData);

            DataModel model = new GenericBooleanPrefDataModel(userData);

            //After create a DataModel object we are able to build a recommender: 
            /*
             * The LogLikelihoodSimilarity class does not need preferences values.
             * Other similarity metrics like Euclidean distance and Pearson
             * correlation throw IllegalArgumentException for boolean preferences. 
             */
            UserSimilarity similarity = new LogLikelihoodSimilarity(model);

            // user-based recommender - considering 2-nearest neighboors, given log-likelihood similarity
            /*
             * Nearest neighborhood means that recommended items for some user will
             * be calculated according to the (log-likelihood) similarity between this user and users contained in model.
             */
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
            long[] sousedi = neighborhood.getUserNeighborhood(Long.parseLong(userId));

            for (int i = 0; i < sousedi.length; i++) {
                System.out.println("soused: " + sousedi[i]);
            }

            //GenericBooleanPrefUserBasedRecommender = the appropriate recommender for boolean preferences
            Recommender recommender = new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);

            // recommended items for specific user (one item recommended)
            List<RecommendedItem> recommendedItems = recommender.recommend(Long.parseLong(userId), limit);

            processRecommendedItems(response, docsToReturn, recommendedItems);
            
            //NEJAK NAPLNIT TY DOCS

        } catch (Exception e) {
            e.printStackTrace();
            //return Response.status(500).entity("error : " + e.toString()).build();
        }
        
        return docs;        
    }
    
    private void fillUserIdsSet(QueryResponse response, Set<Integer> userIdsSet) throws SolrServerException {
        int numFound = (int) response.getResults().getNumFound();
        SolrQuery query = new SolrQuery();
        query.setQuery("id:*");
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
    
    private void createUserData(QueryResponse response, Set<Integer> userIdsSet, FastByIDMap<FastIDSet> userData) throws SolrServerException {
        SolrQuery cfQuery = new SolrQuery();
        cfQuery.setRows(Integer.MAX_VALUE);
        cfQuery.setFields("id");
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
    
    private void processRecommendedItems(QueryResponse response, List<SolrDocument> docsToReturn, List<RecommendedItem> recommendedItems) throws SolrServerException {
        System.out.println("Recommended items for user# " + userId);

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
