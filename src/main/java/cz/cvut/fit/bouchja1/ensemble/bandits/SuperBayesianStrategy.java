/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.bandits;

import cz.cvut.fit.bouchja1.ensemble.bandits.util.MathUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jan
 */
public class SuperBayesianStrategy {

    private String superCollectionId;
    private Set<BayesianStrategy> setOfStrategies;

    public String getSuperCollectionId() {
        return superCollectionId;
    }

    public void setSuperCollectionId(String superCollectionId) {
        this.superCollectionId = superCollectionId;
    }

    public SuperBayesianStrategy(String superCollectionId, Set<BayesianStrategy> setOfStrategies) {
        this.superCollectionId = superCollectionId;
        this.setOfStrategies = setOfStrategies;
    }

    public Set<BayesianStrategy> getSetOfStrategies() {
        return setOfStrategies;
    }

    public void setSetOfStrategies(Set<BayesianStrategy> setOfStrategies) {
        this.setOfStrategies = setOfStrategies;
    }

    //Tady by bylo pomerne jednoduche reseni zeptat se vsech a vybrat ten agoritmus,
    //ktery to doporuci nejvicekrat, pri konfliktu nebo shode prvni nebo s nej hodnotou
    //atd.       
    public String chooseBestFromSuperStrategy() {
        List<Bandit> banditsCombination = new ArrayList<>();
        //projed vsechny bayesovske strategie alias kolekce a zjisti, ktera je tam nejvicekrat
        for (BayesianStrategy strategy : setOfStrategies) {
            Bandit bestFromStrategy = strategy.sampleBandits();
            banditsCombination.add(bestFromStrategy);
        }

        String bestBanditName = bestBanditFromCombination(banditsCombination);                
        return bestBanditName;
    }

    private String bestBanditFromCombination(List<Bandit> banditsCombination) {
        String bestBandit = null;
        SuperBanditArrayBuilder banditCombiner = new SuperBanditArrayBuilder(banditsCombination);
        //nyni mam vytvoreny nastroj pro pocitani unikatnich vyskytu banditu a jednotlivcu
        //kolik mi to doporucilo celkem ruznych algoritmu - 1 za kazdy kontext
        //int uniqueBandits = banditCombiner.getDifferentNamesAmount();        

        Map<String, Integer> uniqueBanditMapSorted = MathUtil.sortMapByValues(banditCombiner.getDifferentBanditsMap());

        //vezmu prvniho nejlepsiho
        for (Map.Entry<String, Integer> entry : uniqueBanditMapSorted.entrySet()) {
            bestBandit = entry.getKey();
            break;
        }
        
        //TODO vyber z vice shodnych
        /*
        int lastValue = 0;
        int currentValue = 0;
        for (Map.Entry<String, Integer> entry : uniqueBanditMapSorted.entrySet()) {
            //entry.getKey();
            //entry.getValue();            
            currentValue = entry.getValue();
            if (currentValue != lastValue) {
                
                break;
            }
        }
        */

        //bude se vracet algoritmus
        return bestBandit;
    }
}
