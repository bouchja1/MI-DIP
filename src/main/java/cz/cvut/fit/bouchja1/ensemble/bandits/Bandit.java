/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.bandits;

import java.util.Objects;

/**
 * The class represents single bandit.
 *
 * @author jan
 *
 */
public class Bandit {

    //pravdepodobnost v case, >0, <1
    private double probability;
    // pokusy - zajima nas pocet uspechu (wins) v n pokusech
    //private Integer trials;
    private double trials;
    //uspechy
    //private Integer successes;
    private double successes;
    private String name;
    //private Integer trialsHistory;
    //private double successesHistory;

    public Bandit(double probability, String name) {
        this.probability = probability;
        this.trials = 0.0;
        this.successes = 0.0;
        this.name = name;
        //this.trialsHistory = 0;
        //this.successesHistory = 0;
    }
    
    public Bandit(double probability, String name, double trials, double successes) {
        this.probability = probability;
        this.trials = trials;
        this.successes = successes;
        this.name = name;
    }    
    
    public void updateTrialStats(double rate, double totalCountsToBoost) {
        double newTrials = rate * trials;
        trials = newTrials + 1;
        probability += totalCountsToBoost;
    }    

    public void updateRoundStats(int result) {
        /*
         * prepociavat pravdepodobnsoti zpetnou vazbou
         */
        successes += result;
        trials++;
    }

    public void updateRoundStatsExtended(double result, double rate) {
        /*
         * rozsirene prepociavat pravdepodobnsoti zpetnou vazbou
         * kvuli rychlejsimu vyvoji situace
         */
        double newSuccesses = rate * successes;
        successes = newSuccesses + result;
        /*
        double newTrials = rate * trials;
        trials = newTrials + 1;
        */
    }
    
    public void updateRoundStatsExtended(double rate) {
        double newSuccesses = rate * successes;
        successes = newSuccesses;
        /*
        double newTrials = rate * trials;
        trials = newTrials + 1;
        */
    }    

    public void updateNegativeRoundStatsExtended(double result, double rate) {
        if (successes > 0.5) {
            double newSuccesses = rate * successes;
            successes = newSuccesses - result;
        }
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    /*
     public Integer getTrials() {
     return trials;
     }

     public void setTrials(Integer trials) {
     this.trials = trials;
     }

     public Integer getSuccesses() {
     return successes;
     }

     public void setSuccesses(Integer successes) {
     this.successes = successes;
     }       
     */
    public double getTrials() {
        return trials;
    }

    public void setTrials(double trials) {
        this.trials = trials;
    }

    public double getSuccesses() {
        return successes;
    }

    public void setSuccesses(double successes) {
        this.successes = successes;
    }

    public String getName() {
        return name;
    }

    /*
    public Integer getTrialsHistory() {
        return trialsHistory;
    }

    public void setTrialsHistory(Integer trialsHistory) {
        this.trialsHistory = trialsHistory;
    }

    public double getSuccessesHistory() {
        return successesHistory;
    }

    public void setSuccessesHistory(double successesHistory) {
        this.successesHistory = successesHistory;
    }
    */

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Bandit other = (Bandit) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
}

