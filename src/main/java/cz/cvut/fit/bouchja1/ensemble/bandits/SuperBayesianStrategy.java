package cz.cvut.fit.bouchja1.ensemble.bandits;

import cz.cvut.fit.bouchja1.ensemble.bandits.util.MathUtil;
import java.util.ArrayList;
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
    public Bandit chooseBestFromSuperStrategy() {
        List<Bandit> banditsCombination = new ArrayList<>();
        //projed vsechny bayesovske strategie alias kolekce a zjisti, ktera je tam nejvicekrat
        for (BayesianStrategy strategy : setOfStrategies) {
            Bandit bestFromStrategy = strategy.sampleBandits();
            banditsCombination.add(bestFromStrategy);
        }

        Bandit bestBanditId = bestBanditFromCombination(banditsCombination);                
        return bestBanditId;
    }

    private Bandit bestBanditFromCombination(List<Bandit> banditsCombination) {
        List<Integer> bestBandits = new ArrayList<>();
        SuperBanditArrayBuilder banditCombiner = new SuperBanditArrayBuilder(banditsCombination);
        //nyni mam vytvoreny nastroj pro pocitani unikatnich vyskytu banditu a jednotlivcu
        //kolik mi to doporucilo celkem ruznych algoritmu - 1 za kazdy kontext
        //int uniqueBandits = banditCombiner.getDifferentNamesAmount();        

        Map<Integer, Integer> uniqueBanditMapSorted = MathUtil.sortMapByValues(banditCombiner.getDifferentBanditsMap());

        int previous=-1;
        //vezmu prvniho nejlepsiho
        for (Map.Entry<Integer, Integer> entry : uniqueBanditMapSorted.entrySet()) {
            //bestBandit = entry.getKey();
            if (bestBandits.isEmpty() || previous==entry.getValue()) {
                bestBandits.add(entry.getKey());
                previous=entry.getValue();
            } else {                
                break;
            }
            
        }       

        int bestBandit = selectBestByGamingStrategy(bestBandits);
        Bandit bestBanditObject = null;
        for (Bandit b : banditsCombination) {
            if (b.getId() == bestBandit) {
                bestBanditObject = b;
                break;
            }
        }
        return bestBanditObject;
        //return banditsCombination.get(bestBandit);
    }

    private Integer selectBestByGamingStrategy(List<Integer> bestBandits) {
        Random randomizer = new Random();
        if (bestBandits.size() > 1) {
            return bestBandits.get(randomizer.nextInt(bestBandits.size() - 1));
        } else return bestBandits.get(0);
    }    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }  
    
}
