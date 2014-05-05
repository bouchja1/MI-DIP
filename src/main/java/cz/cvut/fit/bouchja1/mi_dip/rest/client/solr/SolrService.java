package cz.cvut.fit.bouchja1.mi_dip.rest.client.solr;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.ArticleDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.UserArticleDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ws.rs.WebApplicationException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.schema.DateField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 */
@Component
@Scope("singleton")
public class SolrService {

    private String serverUrl;
    private Map<String, HttpSolrServer> validServers = new HashMap<String, HttpSolrServer>();
    private Set<String> validSolrCores;
    private static final String DOC_ID = "id";
    private static final String ARTICLE_ID = "articleId";
    private static final String ARTICLE_TEXT = "articleText";
    private static final String USER_ID = "userId";
    private static final String GROUP = "group";
    private static final String IMPRESSIONS = "impressions";
    private static final String ALGORITHM = "algorithm";
    private static final String TIME = "time";
    private static final String RECCOMM_FLAG = "usedInRecommendation";

    @PostConstruct
    public void createValidSolrServers() {
        Iterator<String> validCores = validSolrCores.iterator();
        while (validCores.hasNext()) {
            String core = validCores.next();
            validServers.put(core, new HttpSolrServer(serverUrl + core));
        }
    }

    public HttpSolrServer getServerFromPool(String coreId) {
        return validServers.get(coreId);
    }

    public boolean isServerCoreFromPool(String coreId) {
        if (validServers.get(coreId) != null) {
            return true;
        }
        return false;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setValidSolrCores(Set<String> validSolrCores) {
        this.validSolrCores = validSolrCores;
    }

    public SolrDocument isDocumentInIndex(String coreId, String documentId) throws SolrServerException {
        HttpSolrServer server = getServerFromPool(coreId);
        SolrQuery query = new SolrQuery();
        query.setQuery("articleId:\"" + documentId + "\"");
        query.setRows(1);
        QueryResponse response;
        response = server.query(query);
        if (response.getResults().getNumFound() > 0) {
            return response.getResults().get(0);
        } else {
            return null;
        }
    }

    public void deleteDocument(String coreId, String documentId) throws SolrServerException, WebApplicationException, IOException {
        HttpSolrServer server = getServerFromPool(coreId);        
        SolrQuery query = new SolrQuery();
        query.setQuery("articleId:\"" + documentId + "\"");
        query.setRows(1);
        QueryResponse response = server.query(query);
        SolrDocumentList docsList = response.getResults();

        if (!docsList.isEmpty()) {            
            deleteDocument(server, docsList);
        } else {
            throw new WebApplicationException("Document with id " + documentId + " was not found in database.");
        }
    }

    public void putUserArticle(String coreId, UserArticleDocument userArticle) throws SolrServerException, IOException {
        HttpSolrServer server = getServerFromPool(coreId);
        //zjisteni, jestli tam tohle document ID existuje
        SolrQuery query = new SolrQuery();
        query.setQuery("articleId:\"" + userArticle.getArticleId() + "\"");
        query.setRows(1);
        QueryResponse response = server.query(query);
        SolrDocumentList docsList = response.getResults();                

        if (!docsList.isEmpty()) {
            //budeme pridavat usera k dokumentu
            updateSolrDocument(server, docsList, userArticle);
        } else {
            //budeme vytvaret
            createSolrDocument(server, userArticle);
        }
    }

    public void postArticle(String coreId, ArticleDocument article) throws SolrServerException, IOException {
        HttpSolrServer server = getServerFromPool(coreId);
        //zjisteni, jestli tam tohle document ID existuje
        SolrQuery query = new SolrQuery();
        query.setQuery("articleId:\"" + article.getId() + "\"");
        query.setRows(1);
        QueryResponse response = server.query(query);
        SolrDocumentList docsList = response.getResults();

        if (docsList.isEmpty()) {
            SolrInputDocument doc = createArticle(server, article);
            server.add(doc);
            server.commit();
        } else {
            //nebudeme pridavat, protoze uz tam existuje
        }
    }

    private void createSolrDocument(HttpSolrServer server, UserArticleDocument userArticle) throws SolrServerException, IOException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(DOC_ID, SolrAutoIncrementer.getLastIdToUse(server));
        doc.addField(ARTICLE_ID, userArticle.getArticleId());
        doc.addField(USER_ID, userArticle.getUserId());
        doc.addField(GROUP, userArticle.getGroupId());
        doc.addField(userArticle.getUserId() + "_rating", userArticle.getRating());
        doc.addField("weightedRating", userArticle.getRating());
        
        server.add(doc);
        UpdateResponse commit = server.commit();
        NamedList<Object> response = commit.getResponse();
    }

    private SolrInputDocument createArticle(HttpSolrServer server, ArticleDocument article) throws SolrServerException, IOException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(DOC_ID, SolrAutoIncrementer.getLastIdToUse(server));
        doc.addField(ARTICLE_ID, article.getId());
        doc.addField(ARTICLE_TEXT, article.getText());
        doc.addField(GROUP, article.getGroupId());
        doc.addField(IMPRESSIONS, 0);
        doc.addField(TIME, article.getTime());        
        return doc;
    }

    private void updateSolrDocument(HttpSolrServer server, SolrDocumentList docsList, UserArticleDocument userArticle) throws SolrServerException, IOException {
        SolrDocument exisingDoc = docsList.get(0);
        Collection<Object> users = exisingDoc.getFieldValues("userId");
        exisingDoc.removeFields("userId");
        exisingDoc.removeFields("weightedRating");
        Set<Integer> newUsers = new HashSet<Integer>();
        boolean addFlag = false;

        Iterator<Object> i = users.iterator();
        while (i.hasNext()) {
            newUsers.add((Integer) i.next());
        }

        if (newUsers.add(userArticle.getUserId())) {
            addFlag = true;
        }

        exisingDoc.addField("userId", newUsers);

        SolrInputDocument sid = ClientUtils.toSolrInputDocument(exisingDoc);

        if (addFlag) {
            sid.addField(userArticle.getUserId() + "_rating", userArticle.getRating());
        }
        
        //preocitat hodnoceni, poslat tam pocet uzivatelu a jejich hodnoceni                    
        double weightedRating = recalculateWeightedRating(sid);
        exisingDoc.addField("weightedRating", weightedRating);

        server.add(sid);
        UpdateResponse commit = server.commit();
        NamedList<Object> response = commit.getResponse();
    }

    private void deleteDocument(HttpSolrServer server, SolrDocumentList docsList) throws SolrServerException, IOException {
        SolrDocument exisingDoc = docsList.get(0);        
        server.deleteById(String.valueOf(exisingDoc.get("id")));
        server.commit();       
    }

    public void incrementImpression(String coreId, SolrDocumentList results) {
        try {
            HttpSolrServer server = getServerFromPool(coreId);
            for (SolrDocument solrDocument : results) {
                int impression = (int) solrDocument.getFieldValue("impressions");
                impression++;
                solrDocument.setField("impressions", impression);
                SolrInputDocument sid = ClientUtils.toSolrInputDocument(solrDocument);
                server.add(sid);
            }

            server.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postArticles(String coreId, List<ArticleDocument> articles) throws SolrServerException, IOException {
        Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        HttpSolrServer server = getServerFromPool(coreId);
        //zjisteni, jestli tam tohle document ID existuje
        SolrQuery query = new SolrQuery();

        for (ArticleDocument a : articles) {
            query.setQuery("articleId:\"" + a.getId() + "\"");
            query.setRows(1);
            QueryResponse response = server.query(query);
            SolrDocumentList docsList = response.getResults();

            if (docsList.isEmpty()) {
                SolrInputDocument doc1 = createArticle(server, a);
                docs.add(doc1);
            } else {
                continue;
            }
        }

        server.add(docs);
        server.commit();
    }

/*
 R = average for the movie (mean) = (Rating)
v = number of votes for the movie = (votes)
m = minimum votes required to be listed in the Top 250 (currently 3000)
C = the mean vote across the whole report (currently 6.9)
* 
* 
 */         
    private double recalculateWeightedRating(SolrInputDocument sid) {
        Collection<Object> usersNew = sid.getFieldValues("userId");    
        Iterator<Object> i = usersNew.iterator();
        List<Integer> userIds = new ArrayList<>();
        while (i.hasNext()) {
            userIds.add((Integer) i.next());
        }
        double ratingSum = 0.0;
        for (int j = 0; j < userIds.size(); j++) {
            float f = (float) sid.getFieldValue(userIds.get(j) + "_rating");
            double d = f;
            ratingSum = ratingSum + d; 
        }
        
        int minimumVotesrequired = 50;
        double meanVoteAcrossWhole = 3.25;
        
        double weightedRate = (((ratingSum / (double) userIds.size()) * (double) userIds.size()) + (double)minimumVotesrequired*meanVoteAcrossWhole) / ((double) userIds.size() + (double)minimumVotesrequired);
        return weightedRate;
    }
}
