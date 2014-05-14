package cz.cvut.fit.bouchja1.ensemble.bandits;

import java.util.Map;
import org.springframework.core.env.Environment;

/**
 * The class represents N bandits machines.
 *
 * @author jan
 *
 */
public class BanditsMachine {

    //private List<Bandit> banditList; // seznam banditu
    private Map<Integer, Bandit> banditList; // seznam banditu
    private double rate;
    private double winner;
    private double losers;
    private double stupid;
    
    public BanditsMachine(Map<Integer, Bandit> banditList, double rate) {
        this.banditList = banditList;
        this.rate = rate;
    }
    
    public BanditsMachine(Map<Integer, Bandit> banditList, Environment env) {
        this.banditList = banditList;
    this.rate = Double.parseDouble(env.getProperty("ensemble.machine.rate"));
    this.winner = Double.parseDouble(env.getProperty("ensemble.feedback.possitive.winner"));
    this.losers = Double.parseDouble(env.getProperty("ensemble.feedback.possitive.losers"));
    this.stupid = Double.parseDouble(env.getProperty("ensemble.feedback.negative.stupid"));
    }     

    /*
     * return the results, 0 or 1, of pulling the banditArmth bandit.
     * 
     * Vstup z vencni, zda na to kliknul nebo ne
     */
    /*
    public int pull(int banditIndex) {
        // banditArm which arm to pull
        int reward;

        if (counter < pokusy.length()) {
            reward = Integer.valueOf(Character.toString(pokusy.charAt(counter)));
        } else {
            reward = 0;
        }

        counter++;
        return reward;
    }
    */

    public Bandit getBanditAtIndex(int i) {
        return banditList.get(i);
    }

    /*
    public List<Bandit> getBanditList() {
    return banditList;
    }
     */
    public Map<Integer, Bandit> getBanditList() {
        return banditList;
    }       
    
    public void updateTrials(Bandit banditToUpdate, double totalTrialsCountsToBoost) {
        banditToUpdate.updateTrialStats(rate, totalTrialsCountsToBoost);                           
    }    

    public void updateFeedback(Bandit banditToUpdate, String feedback) {        
        
        switch (feedback) {
            case "possitive" :
                //double banditSuccessesPossitiveRate = (double) 1 / (double) (banditList.size() - 1);        
                //double totalSuccesessCountsToBoost = recalculateSuccessessFrequencies(banditSuccessesPossitiveRate);                                       
                makePositiveFeedback(banditToUpdate);
                break;
            case "negative" :
                //double banditSuccessesNegativeRate = (double) 1 / (double) (banditList.size() - 1);
                //recalculateSuccessessNegativeFrequencies(banditSuccessesNegativeRate, banditToUpdate);                                                       
                makeNegativeFeedback(banditToUpdate);
                break;
        }            
    }

    private void makePositiveFeedback(Bandit banditToUpdate) {
        //tomu, ktery dal dobre doporuceni, pricteme pozitivni zpetnou vazbu
        banditToUpdate.updateRoundStatsExtended(winner, rate);
        //banditToUpdate.setNormalizedSuccessFrequencyInTime(banditToUpdate.getNormalizedSuccessFrequencyInTime() + totalSuccesessCountsToBoost);
        //vsem ostatnim v nejakem pomeru snizime
        for (Map.Entry<Integer, Bandit> b : banditList.entrySet()) {
            if (!b.getValue().equals(banditToUpdate)) {
                b.getValue().updateNegativeRoundStatsExtended(losers, rate);
            }
        }        
    }

    private void makeNegativeFeedback(Bandit banditToUpdate) {
        if (banditToUpdate.getSuccesses() > (stupid*rate)) {
            banditToUpdate.updateRoundStatsExtendedStupid(stupid, rate);
        } else {
            banditToUpdate.updateRoundStatsExtended(rate);
        }
    }

    public void addBanditToMachine(Bandit b) {
        //banditList.add(b);
        banditList.put(b.getId(), b);
    }

    double recalculateTrialsFrequencies(double banditTrialsProbabilityRate) {
        double totalPartialyProbabilities = 0.0;
        for (Map.Entry<Integer, Bandit> b : banditList.entrySet()) {
            double result = b.getValue().getNormalizedTrialsFrequencyInTime() * banditTrialsProbabilityRate;
            totalPartialyProbabilities += result;
            b.getValue().setNormalizedTrialsFrequencyInTime(b.getValue().getNormalizedTrialsFrequencyInTime() - result);
        }
        return totalPartialyProbabilities;
    }
    
    double recalculateSuccessessFrequencies(double banditSuccessesProbabilityRate) {
        double totalPartialyProbabilities = 0.0;
        for (Map.Entry<Integer, Bandit> b : banditList.entrySet()) {
            double result = b.getValue().getNormalizedSuccessFrequencyInTime()* banditSuccessesProbabilityRate;
            totalPartialyProbabilities += result;
            b.getValue().setNormalizedTrialsFrequencyInTime(b.getValue().getNormalizedSuccessFrequencyInTime() - result);
        }
        return totalPartialyProbabilities;
    }  
    
    void recalculateSuccessessNegativeFrequencies(double banditSuccessesProbabilityRate, Bandit banditToUpdatee) {
        double rateMinus = banditToUpdatee.getNormalizedSuccessFrequencyInTime() * banditSuccessesProbabilityRate;
        double rateToOthers = rateMinus * banditSuccessesProbabilityRate;
        for (Map.Entry<Integer, Bandit> b : banditList.entrySet()) {
            if (!b.equals(banditToUpdatee)) {                
                b.getValue().setNormalizedTrialsFrequencyInTime(b.getValue().getNormalizedSuccessFrequencyInTime() + rateToOthers);                
            } else {
                banditToUpdatee.setNormalizedSuccessFrequencyInTime(banditToUpdatee.getNormalizedSuccessFrequencyInTime() - rateMinus);
            } 
        }
    }      
}

