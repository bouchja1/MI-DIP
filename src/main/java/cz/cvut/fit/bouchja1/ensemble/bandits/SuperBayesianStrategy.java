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
import java.util.Random;
import java.util.Set;

/**
 *
 * @author jan
 */
public class SuperBayesianStrategy {

    private int id;
    private String superCollectionId;
    private Set<BayesianStrategy> setOfStrategies;

    public SuperBayesianStrategy(String superCollectionId) {
        this.superCollectionId = superCollectionId;
    }
    
    public SuperBayesianStrategy(int id, String superCollectionId) {
        this.id = id;
        this.superCollectionId = superCollectionId;
    }    
    
    public SuperBayesianStrategy(int id, String superCollectionId, Set<BayesianStrategy> setOfStrategies) {
        this.id = id;
        this.superCollectionId = superCollectionId;
        this.setOfStrategies = setOfStrategies;
    }    

    public String getSuperCollectionId() {
        return superCollectionId;
    }

    public void setSuperCollectionId(String superCollectionId) {
        this.superCollectionId = superCollectionId;
    }

    public Set<BayesianStrategy> getSetOfStrategies() {
        return setOfStrategies;
    }
    
    public void addStrategyToSuperstrategy(BayesianStrategy strategy) {
        setOfStrategies.add(strategy);
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
        //String bestBandit = null;
        List<String> bestBandits = new ArrayList<>();
        SuperBanditArrayBuilder banditCombiner = new SuperBanditArrayBuilder(banditsCombination);
        //nyni mam vytvoreny nastroj pro pocitani unikatnich vyskytu banditu a jednotlivcu
        //kolik mi to doporucilo celkem ruznych algoritmu - 1 za kazdy kontext
        //int uniqueBandits = banditCombiner.getDifferentNamesAmount();        

        Map<String, Integer> uniqueBanditMapSorted = MathUtil.sortMapByValues(banditCombiner.getDifferentBanditsMap());

        int previous=-1;
        //vezmu prvniho nejlepsiho
        for (Map.Entry<String, Integer> entry : uniqueBanditMapSorted.entrySet()) {
            //bestBandit = entry.getKey();
            if (bestBandits.isEmpty() || previous==entry.getValue()) {
                bestBandits.add(entry.getKey());
                previous=entry.getValue();
            } else {                
                break;
            }
            
        }       

        String bestBandit = selectBestByGamingStrategy(bestBandits);
        return bestBandit;
    }

    private String selectBestByGamingStrategy(List<String> bestBandits) {
        Random randomizer = new Random();
        return bestBandits.get(randomizer.nextInt(bestBandits.size()));
    }    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }  
    
}
