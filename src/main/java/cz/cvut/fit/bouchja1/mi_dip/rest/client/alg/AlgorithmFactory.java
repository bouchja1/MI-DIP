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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jan
 */
public class AlgorithmFactory {

    /*
     * TODO: pro ucely odevzdani je to takto prasacky.. dodelat pres HashMapu, klice a reflexi
     * Class.getDeclaredConstructor(Class class).newInstance(argumenty oddelene carkou);
     */    
    public static final String[] SET_VALUES = new String[]{"random", "latest", "mlt", "toprate", "cfuser", "cfitem"};
    public static final List<String> SUPPORTED_ALGORITHM = new ArrayList<>(Arrays.asList(SET_VALUES));   
    
    public static IAlgorithm getAlgorithm(String algorithmId, Map<String, String> algorithmParams) {

        switch (algorithmId) {
            case "random":
                return new AlgorithmRandom(algorithmParams);
            case "latest":
                return new AlgorithmLatest(algorithmParams);
            case "mlt":
                return new AlgorithmMlt(algorithmParams);
            case "toprate":
                return new AlgorithmWeightedRating(algorithmParams);
            case "cfuser":
                return new AlgorithmUserBasedCf(algorithmParams);
            case "cfitem":
                return new AlgorithmItemBasedCf(algorithmParams);
            default:
                return null;
        }
    }

    public static List<String> getSUPPORTED_ALGORITHM() {
        return SUPPORTED_ALGORITHM;
    }
        
}
