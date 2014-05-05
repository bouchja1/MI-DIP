/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.alg;


import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.impl.AlgorithmItemBasedCf;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.impl.AlgorithmLatest;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.impl.AlgorithmMlt;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.impl.AlgorithmRandom;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.impl.AlgorithmUserBasedCf;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.alg.impl.AlgorithmWeightedRating;
import java.util.Map;

/**
 *
 * @author jan
 */
public class AlgorithmFactory {
    public static IAlgorithm getAlgorithm(String algorithmId, Map<String, String> algorithmParams) {
        switch (algorithmId) {
            case "random" :
                return new AlgorithmRandom(algorithmParams);            
            case "latest" : 
                return new AlgorithmLatest(algorithmParams);                  
            case "mlt" : 
                return new AlgorithmMlt(algorithmParams);   
            case "toprate" : 
                return new AlgorithmWeightedRating(algorithmParams);   
            case "cfuser" : 
                return new AlgorithmUserBasedCf(algorithmParams);  
            case "cfitem" : 
                return new AlgorithmItemBasedCf(algorithmParams);                   
            default : 
                return null;
        }
    }    
}
