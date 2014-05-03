/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.alg;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.AlgorithmEndpointHelper;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.AlgorithmSolrService;
import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public interface IAlgorithm {

    public Response recommend(AlgorithmSolrService algorithmSolrService, AlgorithmEndpointHelper helper);
    
}
