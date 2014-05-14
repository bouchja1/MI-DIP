package cz.cvut.fit.bouchja1.ensemble.bandits;

import cz.cvut.fit.bouchja1.ensemble.bandits.util.MathUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.distribution.BetaDistribution;
/**
 * Implements a online, learning strategy to solve the Multi-Armed Bandit
 * problem.
 *
 * @author jan
 */
public class BayesianStrategy {

    private int id;
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
    
    public BayesianStrategy(int id, String collectionId, BanditsMachine bandits) {
        this.id = id;
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
    for (Integer bandit : banditsMachine.getBanditList().keySet()) {        
        BetaDistribution beta = new BetaDistribution(1 + banditsMachine.getBanditAtIndex(bandit).getSuccesses(), 1 + banditsMachine.getBanditAtIndex(bandit).getTrials() - banditsMachine.getBanditAtIndex(bandit).getSuccesses());

        //tím se simuluje náhodný výběr s danou pravděpodobností                
        //pomocí funkce se vypocita prislusna hodnotu, pro kterou dostanu tuhle pravděpodobnost
        //Random gen = new MersenneTwisterRNG();
        //http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/distribution/AbstractRealDistribution.html#inverseCumulativeProbability(double)
        double inverseDistribution = beta.inverseCumulativeProbability(Math.random());

        System.out.println("DISTRIBUCE bandity " + banditsMachine.getBanditAtIndex(bandit).getName() + ": " + inverseDistribution);
        roundInverseDistributions.add(inverseDistribution);
    }

    int banditIndexChoice = MathUtil.argmax(roundInverseDistributions);

    roundInverseDistributions.clear();

    return banditsMachine.getBanditAtIndex(banditIndexChoice);
}
        
    public List<Bandit> sampleBanditsAll(String banditCollectionId) {
        for (Integer bandit : banditsMachine.getBanditList().keySet()) {
            BetaDistribution beta = new BetaDistribution(1 + banditsMachine.getBanditAtIndex(bandit).getSuccesses(), 1 + banditsMachine.getBanditAtIndex(bandit).getTrials() - banditsMachine.getBanditAtIndex(bandit).getSuccesses());

            double inverseDistribution = beta.inverseCumulativeProbability(Math.random());

            System.out.println("DISTRIBUCE bandity " + banditsMachine.getBanditAtIndex(bandit).getName() + ": " + inverseDistribution);
            roundInverseDistributions.add(inverseDistribution);            
        }        
        
        int[] banditIndexChoice = MathUtil.sortedArgmax(roundInverseDistributions);

        roundInverseDistributions.clear();
       
        List<Bandit> bandits = new ArrayList<>();
        for (int i = 0; i < banditIndexChoice.length;i++) {
            bandits.add(banditsMachine.getBanditAtIndex(banditIndexChoice[i]+1));
        }

        return bandits;
    }

    public void selectBandit(String banditId) {
        Bandit banditToUpdate = banditsMachine.getBanditAtIndex(Integer.parseInt(banditId));
        
        double banditTrialsProbabilityRate = (double) 1 / (double) (banditsMachine.getBanditList().size() - 1);        
        double totalTrialsCountsToBoost = banditsMachine.recalculateTrialsFrequencies(banditTrialsProbabilityRate);
        
        //pricti banditovi, ze byl vybran
        banditsMachine.updateTrials(banditToUpdate, totalTrialsCountsToBoost);           
    }
    
    public void calculateFeedback(String banditCollectionId, String banditId, String feedbackValue) {
        Bandit banditToUpdate = banditsMachine.getBanditAtIndex(Integer.parseInt(banditId));
        banditsMachine.updateFeedback(banditToUpdate, feedbackValue);   
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
        for (Integer bandit : banditsMachine.getBanditList().keySet()) {
            if (bandit == Integer.parseInt(banditId)) return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
       

}


