/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.impl;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.IAlgorithm;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.helper.AlgorithmEndpointHelper;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.solr.AlgorithmSolrService;
import java.util.Map;
import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public class AlgorithmWeightedRating implements IAlgorithm {
    
    private static final String ALGORITHM_NAME = "toprate";
    private String id;

    public AlgorithmWeightedRating(Map<String, String> algorithmParams) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //http://stackoverflow.com/questions/2134504/what-is-the-best-algorithm-to-calculate-the-most-scored-item
    //http://www.imdb.com/chart/top
    //http://en.wikipedia.org/wiki/Internet_Movie_Database#User_ratings_of_films
    
    @Override
    public Response recommend(AlgorithmSolrService algorithmSolrService, AlgorithmEndpointHelper helper) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getId() {
        return id;
    }
    
    
}
