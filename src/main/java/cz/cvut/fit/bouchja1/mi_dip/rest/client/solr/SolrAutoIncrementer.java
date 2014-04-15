/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

/**
 *
 * @author jan
 */
public class SolrAutoIncrementer {

    public static long getLastIdToUse(HttpSolrServer server) throws SolrServerException {
        //Query to check how many documents are in index
        long lastAutoincrementId;
        SolrQuery aiQuery = new SolrQuery();
        aiQuery.setQuery("id:*");
        aiQuery.setRows(0);
        QueryResponse response = server.query(aiQuery);
        long docsCount = response.getResults().getNumFound();
        
        //Check documents greater than docsCount
        aiQuery.setQuery("id:[" + docsCount + " TO *]");
        aiQuery.setRows(1);
        aiQuery.setSortField("id", SolrQuery.ORDER.desc);
        response = server.query(aiQuery);                
        
        if (!response.getResults().isEmpty()) {
            lastAutoincrementId = Long.valueOf(response.getResults().get(0).getFieldValue("id")+"");
            lastAutoincrementId++;
        } else {
            //first run - empty index
            lastAutoincrementId = 1;
        }
        
        
        return lastAutoincrementId;
    }
}
