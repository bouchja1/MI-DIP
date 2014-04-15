package cz.cvut.fit.bouchja1.mi_dip.rest.client.solr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
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
    
    protected HttpSolrServer getServerFromPool(String coreId) {
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
}
