/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.solr;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.UserArticleDocument;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 */
@Component
@Scope("singleton")
public class CoreSolrService {
    
    @Autowired
    private SolrService solrService;

    private static final String DOC_ID = "id";
    private static final String ARTICLE_ID = "articleId";
    private static final String ARTICLE_TEXT = "articleText";
    private static final String USER_ID = "userId";
    private static final String GROUP = "group";
    private static final String TIME = "time";
    private static final String RECCOMM_FLAG = "usedInRecommendation";

    public void disableArticle(String coreId, String documentId) throws SolrServerException, WebApplicationException, IOException {
        HttpSolrServer server = solrService.getServerFromPool(coreId);
        SolrQuery query = new SolrQuery();
        query.setQuery("articleId:" + documentId);
        query.setRows(1);
        QueryResponse response = server.query(query);
        SolrDocumentList docsList = response.getResults();

        if (!docsList.isEmpty()) {
            disableDocument(server, docsList);
        } else {
            throw new WebApplicationException("Document with id " + documentId + "was not found in database.");
        }
    }

    public void putUserArticle(String coreId, UserArticleDocument userArticle) throws SolrServerException, IOException {
        HttpSolrServer server = solrService.getServerFromPool(coreId);
        //zjisteni, jestli tam tohle document ID existuje
        SolrQuery query = new SolrQuery();
        query.setQuery("articleId:" + userArticle.getArticleId());
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

    private void createSolrDocument(HttpSolrServer server, UserArticleDocument userArticle) throws SolrServerException, IOException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(DOC_ID, SolrAutoIncrementer.getLastIdToUse(server));
        doc.addField(ARTICLE_ID, userArticle.getArticleId());
        doc.addField(ARTICLE_TEXT, userArticle.getArticleText());
        doc.addField(GROUP, userArticle.getGroup());
        doc.addField(USER_ID, userArticle.getUserId());
        doc.addField(RECCOMM_FLAG, true);
        doc.addField(TIME, userArticle.getTime());
        doc.addField(userArticle.getUserId() + "_rating", userArticle.getRating());
        server.add(doc);
        UpdateResponse commit = server.commit();
        NamedList<Object> response = commit.getResponse();
    }

    private void updateSolrDocument(HttpSolrServer server, SolrDocumentList docsList, UserArticleDocument userArticle) throws SolrServerException, IOException {
        SolrDocument exisingDoc = docsList.get(0);
        Collection<Object> users = exisingDoc.getFieldValues("userId");
        exisingDoc.removeFields("userId");
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

        server.add(sid);
        UpdateResponse commit = server.commit();
        NamedList<Object> response = commit.getResponse();
    }

    private void disableDocument(HttpSolrServer server, SolrDocumentList docsList) throws SolrServerException, IOException {
        SolrDocument exisingDoc = docsList.get(0);
        exisingDoc.setField("usedInRecommendation", false);
        SolrInputDocument sid = ClientUtils.toSolrInputDocument(exisingDoc);
        server.add(sid);
        UpdateResponse commit = server.commit();
    }

    public SolrService getSolrService() {
        return solrService;
    }
    
    
}
