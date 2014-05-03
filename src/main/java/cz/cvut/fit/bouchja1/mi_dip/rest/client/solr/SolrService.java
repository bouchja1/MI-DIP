package cz.cvut.fit.bouchja1.mi_dip.rest.client.solr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
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
        query.setQuery("articleId:"+documentId);
        query.setRows(1);
        QueryResponse response;
        response = server.query(query);
        if (response.getResults().getNumFound() > 0) {
            return response.getResults().get(0);
        } else return null;
    }
}
