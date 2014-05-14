package cz.cvut.fit.bouchja1.mi_dip.rest.client.alg;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.AlgorithmEndpointHelper;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.SolrService;

import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public interface IAlgorithm {

    public Response recommend(SolrService solrService, AlgorithmEndpointHelper helper);
    
}
