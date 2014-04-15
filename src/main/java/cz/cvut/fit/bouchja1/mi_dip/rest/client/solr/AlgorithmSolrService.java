/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.solr;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.OutputDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.UserIdDocument;
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

    public List<OutputDocument> getRecommendationByRandom(String coreId, int limit) throws SolrServerException {
        HttpSolrServer server = solrService.getServerFromPool(coreId);
        List<OutputDocument> docs = new ArrayList<OutputDocument>();
        int random = generator.nextInt(Integer.MAX_VALUE) + 1; // values are between 1 and Integer.MAX_VALUE
        String sortOrder = "random_" + random;
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setFilterQueries("usedInRecommendation:true");
        query.setRows(limit);
        query.setSortField(sortOrder, SolrQuery.ORDER.desc);

        QueryResponse response;
        response = server.query(query);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            System.out.println(results.get(i));
            OutputDocument output = new OutputDocument();
            output.setDocumentId((String) results.get(i).getFieldValue("articleId"));
            output.setArticleText((String) results.get(i).getFieldValue("articleText"));
            output.setGroup(Integer.valueOf(results.get(i).getFieldValue("group")+""));
            output.setTime((Date) results.get(i).getFieldValue("time"));
            Set<UserIdDocument> usersToDoc = new HashSet<UserIdDocument>();
            Collection<Object> users = results.get(i).getFieldValues("userId");
            Iterator<Object> usersIterator = users.iterator();
            while (usersIterator.hasNext()) {
                Integer userId = Integer.valueOf(usersIterator.next()+"");
                usersToDoc.add(new UserIdDocument(userId));
            }
            output.setUser(usersToDoc);
            docs.add(output);
        }
        return docs;
    }
}
