/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.bandits;

import cz.cvut.fit.bouchja1.ensemble.bandits.util.MathUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.distribution.BetaDistribution;
/**
 * Implements a online, learning strategy to solve the Multi-Armed Bandit
 * problem.
 *
 * sources:
 *
 * http://stackoverflow.com/questions/12034782/how-to-calculate-the-inverse-cumulative-beta-distribution-function-in-java
 *
 *
 * @author jan
 */
public class BayesianStrategy {

    private String collectionId;
    private BanditsMachine banditsMachine;
    // the cumulative number of samples
    private int samplesNumber;
    // the historical choices as a (N,) array
    //private List<Integer> pastChoices;
    // the historical score as a (N,) array
    //private List<Integer> pastBbScore;
    private List<Double> roundInverseDistributions;

    public BayesianStrategy(String collectionId, BanditsMachine bandits) {
        this.collectionId = collectionId;
        this.banditsMachine = bandits;
        // the cumulative number of samples
        this.samplesNumber = 0;
        //this.pastChoices = new ArrayList<>();
        //this.pastBbScore = new ArrayList<>();
        this.roundInverseDistributions = new ArrayList<>();
    }

    public Bandit sampleBandits() {
        //sample from the bandits's priors, and select the largest sample
        for (int j = 0; j < banditsMachine.getBanditList().size(); j++) {
            // the observed result X (a win or loss, encoded 1 and 0 respectfully) is Binomial,
            //the posterior is a Beta(α=1+X,β=1+1−X) (see here for why to is true). 
            BetaDistribution beta = new BetaDistribution(1 + banditsMachine.getBanditAtIndex(j).getSuccesses(), 1 + banditsMachine.getBanditAtIndex(j).getTrials() - banditsMachine.getBanditAtIndex(j).getSuccesses());

            //tím se simuluje náhodný výběr s danou pravděpodobností                
            //pomocí funkce spočítáš příslušnou hodnotu, pro kterou dostaneš tuhle pravděpodobnost

            // Math.random = greater than or equal to 0.0 and less than 1.0
            // to je to, kdyz strili na tu osu x v nacrtku rozdeleni

            // misto math random - mersenne twister, XOR SHIFT nebo well-rng 512 ?

            //Random gen = new MersenneTwisterRNG();
            //http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/distribution/AbstractRealDistribution.html#inverseCumulativeProbability(double)
            double inverseDistribution = beta.inverseCumulativeProbability(Math.random());

            System.out.println("DISTRIBUCE bandity " + banditsMachine.getBanditAtIndex(j).getName() + ": " + inverseDistribution);
            roundInverseDistributions.add(inverseDistribution);
        }
        
        int banditIndexChoice = MathUtil.argmax(roundInverseDistributions);

        roundInverseDistributions.clear();
        
        return banditsMachine.getBanditAtIndex(banditIndexChoice);
    }
        
    public List<Bandit> sampleBanditsAll(String banditCollectionId) {
        for (int j = 0; j < banditsMachine.getBanditList().size(); j++) {            
            BetaDistribution beta = new BetaDistribution(1 + banditsMachine.getBanditAtIndex(j).getSuccesses(), 1 + banditsMachine.getBanditAtIndex(j).getTrials() - banditsMachine.getBanditAtIndex(j).getSuccesses());

            double inverseDistribution = beta.inverseCumulativeProbability(Math.random());

            System.out.println("DISTRIBUCE bandity " + banditsMachine.getBanditAtIndex(j).getName() + ": " + inverseDistribution);
            roundInverseDistributions.add(inverseDistribution);
        }        

        int[] banditIndexChoice = MathUtil.sortedArgmax(roundInverseDistributions);

        roundInverseDistributions.clear();
       
        List<Bandit> bandits = new ArrayList<>();
        for (int i = 0; i < banditIndexChoice.length;i++) {
            bandits.add(banditsMachine.getBanditAtIndex(banditIndexChoice[i]));
        }

        return bandits;
    }

    public void selectBandit(String banditId) {
        int banditIndexChoice = Integer.valueOf(banditId);
        Bandit banditToUpdate = banditsMachine.getBanditAtIndex(banditIndexChoice);
        
        double banditTrialsProbabilityRate = (double) 1 / (double) (banditsMachine.getBanditList().size() - 1);
        
        double totalCountsToBoost = banditsMachine.recalculateProbabilities(banditTrialsProbabilityRate);
        
        //pricti banditovi, ze byl vybran
        banditsMachine.updateTrials(banditToUpdate, totalCountsToBoost);           
    }
    
    public void calculateFeedback(String banditCollectionId, String banditId, String feedbackValue) {
        int banditIndexChoice = Integer.valueOf(banditId);
        Bandit banditToUpdate = banditsMachine.getBanditAtIndex(banditIndexChoice);
        banditsMachine.updateAllStatsInRound(banditToUpdate, feedbackValue);   
    }    

    private void printRound() {
        System.out.println("kroku: " + samplesNumber + "\n");

        for (int j = 0; j < banditsMachine.getBanditList().size(); j++) {
            System.out.println("bandita: " + banditsMachine.getBanditAtIndex(j).getName());
            //System.out.println("pulls: " + banditsMachine.getBanditAtIndex(j).getTrialsHistory());
            //System.out.println("reward: " + banditsMachine.getBanditAtIndex(j).getSuccessesHistory());
            System.out.println("");
        }

        System.out.println("");
    }

    public BanditsMachine getBanditsMachine() {
        return banditsMachine;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public boolean existBandit(String banditId) {
        for (Bandit b : banditsMachine.getBanditList()) {
            if (b.getName().equals(banditId)) return true;
        }
        return false;
    }
       

}

