/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.bandits;

import cz.cvut.fit.bouchja1.ensemble.bandits.util.MathUtil;
import java.util.List;
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
    private Map<String, Bandit> banditList; // seznam banditu
    private double rate;
    private double possitiveFeedback;
    private double possitiveFeedbackOthers;
    private double negativeFeedback;

    /*
    public BanditsMachine(List<Bandit> banditList, double rate) {
        this.banditList = banditList;
        this.rate = rate;
    }
    
    public BanditsMachine(List<Bandit> banditList, Environment env) {
        this.banditList = banditList;
        this.rate = Double.parseDouble(env.getProperty("ensemble.machine.rate"));
        this.possitiveFeedback = Double.parseDouble(env.getProperty("ensemble.feedback.possitive.best"));
        this.possitiveFeedbackOthers = Double.parseDouble(env.getProperty("ensemble.feedback.possitive.others"));
        this.negativeFeedback = Double.parseDouble(env.getProperty("ensemble.feedback.negative"));
    } 
    */
    
    public BanditsMachine(Map<String, Bandit> banditList, double rate) {
        this.banditList = banditList;
        this.rate = rate;
    }
    
    public BanditsMachine(Map<String, Bandit> banditList, Environment env) {
        this.banditList = banditList;
        this.rate = Double.parseDouble(env.getProperty("ensemble.machine.rate"));
        this.possitiveFeedback = Double.parseDouble(env.getProperty("ensemble.feedback.possitive.best"));
        this.possitiveFeedbackOthers = Double.parseDouble(env.getProperty("ensemble.feedback.possitive.others"));
        this.negativeFeedback = Double.parseDouble(env.getProperty("ensemble.feedback.negative"));
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
    public Map<String, Bandit> getBanditList() {
        return banditList;
    }       
    
    public void updateTrials(Bandit banditToUpdate, double totalCountsToBoost) {
        banditToUpdate.updateTrialStats(rate, totalCountsToBoost);                           
    }    

    /*
     * zpetna vazba
     * - pokud je DOBRE doporuceni
     *      - tomu, ktery byl vybran, se pricte +1
     *      - ostatni se neco malo v pomeru odecte
     * - pokud je SPATNE doporuceni
     *      - jemu se trochu odecte (preci jen byl povazovat do te doby za nejlepsiho)
     *      - ostatni zustanou stejne (at maji sanci stat se tako v budoucnu doporucovanymi)
     */
    public void updateAllStatsInRound(Bandit banditToUpdate, String feedback) {
        //banditToUpdate.updateRoundStats(result);        
        
        /*
         * TODO rekalkulace feedbacku do tech intervalu
         * - dle poctu hvezdicek pozitivni, neutralni nebo negativni
         */
        
        switch (feedback) {
            case "possitive" :
                makePositiveFeedback(banditToUpdate);
                break;
            case "negative" :
                makeNegativeFeedback(banditToUpdate);
                break;
        }            
    }

    private void makePositiveFeedback(Bandit banditToUpdate) {
        //tomu, ktery dal dobre doporuceni, pricteme pozitivni zpetnou vazbu
        banditToUpdate.updateRoundStatsExtended(possitiveFeedback, rate);

        //vsem ostatnim v nejakem pomeru snizime
        for (Map.Entry<String, Bandit> b : banditList.entrySet()) {
            if (!b.getValue().equals(banditToUpdate)) {
                b.getValue().updateNegativeRoundStatsExtended(possitiveFeedbackOthers, rate);
            }
        }        
    }

    private void makeNegativeFeedback(Bandit banditToUpdate) {
        if (banditToUpdate.getSuccesses() > negativeFeedback) {
            banditToUpdate.updateRoundStatsExtended(-negativeFeedback, rate);
        } else {
            banditToUpdate.updateRoundStatsExtended(rate);
        }
    }

    public void addBanditToMachine(Bandit b) {
        //banditList.add(b);
        banditList.put(b.getName(), b);
    }

    double recalculateProbabilities(double banditTrialsProbabilityRate) {
        double totalPartialyProbabilities = 0.0;
        for (Map.Entry<String, Bandit> b : banditList.entrySet()) {
            double result = b.getValue().getProbability() * banditTrialsProbabilityRate;
            totalPartialyProbabilities += result;
            b.getValue().setProbability(b.getValue().getProbability() - result);
        }
        return totalPartialyProbabilities;
    }

    Bandit getBanditByKey(String banditId) {
        return banditList.get(banditId);
    }
}

