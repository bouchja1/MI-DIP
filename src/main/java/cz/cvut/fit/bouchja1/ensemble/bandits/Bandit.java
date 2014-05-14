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
    private double normalizedTrialsFrequencyInTime;
    private double normalizedSuccessFrequencyInTime;
    // pokusy
    private double trials;
    //uspechy
    private double successes;
    private String name;
    private int id;

    public Bandit(double normalizedTrialsFrequencyInTime, double normalizedSuccessFrequencyInTime, String name, int id) {
        this.normalizedTrialsFrequencyInTime = normalizedTrialsFrequencyInTime;
        this.normalizedSuccessFrequencyInTime = normalizedSuccessFrequencyInTime;
        this.trials = 0.0;
        this.successes = 0.0;
        this.name = name;
        this.id = id;
        //this.trialsHistory = 0;
        //this.successesHistory = 0;
    }
    
    public Bandit(double normalizedTrialsFrequencyInTime, double normalizedSuccessFrequencyInTime, String name, int id, double trials, double successes) {
        this.normalizedTrialsFrequencyInTime = normalizedTrialsFrequencyInTime;
        this.normalizedSuccessFrequencyInTime = normalizedSuccessFrequencyInTime;
        this.trials = trials;
        this.successes = successes;
        this.name = name;
        this.id = id;
    } 
    
    public Bandit(String name, int id, double trials, double successes) {
        this.trials = trials;
        this.successes = successes;
        this.name = name;
        this.id = id;
    }     
    
    /*
     * zpetna vazba pro nacitani pokusu
     */
    public void updateTrialStats(double rate, double totalTrialsCountsToBoost) {
        double newTrials = rate * trials;
        trials = newTrials + 1;
        normalizedTrialsFrequencyInTime += totalTrialsCountsToBoost;
    }    

    /*
     * zpetna vazba pro pricitani uspechu zvoleneho algoritmu
     */
    public void updateRoundStatsExtended(double winner, double rate) {
        double newSuccesses = rate * successes;
        successes = newSuccesses + winner;
    }
    
    /*
     * odebirani v pomeru jako penalizace za spatne doporuceni
     */
    public void updateRoundStatsExtendedStupid(double stupid, double rate) {
        successes = successes - (stupid*rate);
        double newSuccesses = rate * successes;
        successes = newSuccesses;
    }    
    
    /*
     * v pripade, ze pri udelovani negativni zpetne vazby neni co odebirat. Tak se jen prepocte rate
     */
    public void updateRoundStatsExtended(double rate) {
        double newSuccesses = rate * successes;
        successes = newSuccesses;
    }    

    /*
     * odecteni v pomeru ostatnim algoritmum, ktere nebyly pro doporuceni vybrany
     */
    public void updateNegativeRoundStatsExtended(double losers, double rate) {
        if (successes > (losers*rate)) {
            successes = successes - (losers*rate);
            double newSuccesses = rate * successes;
            successes = newSuccesses;
        } else {
            double newSuccesses = rate * successes;
            successes = newSuccesses;            
        }
    }

    public double getNormalizedTrialsFrequencyInTime() {
        return normalizedTrialsFrequencyInTime;
    }

    public void setNormalizedTrialsFrequencyInTime(double normalizedTrialsFrequencyInTime) {
        this.normalizedTrialsFrequencyInTime = normalizedTrialsFrequencyInTime;
    }

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

    @Override
    public String toString() {
        return "Bandit{ id: " + id + ", name: " + name + '}';
    } 

    public double getNormalizedSuccessFrequencyInTime() {
        return normalizedSuccessFrequencyInTime;
    }

    public void setNormalizedSuccessFrequencyInTime(double normalizedSuccessFrequencyInTime) {
        this.normalizedSuccessFrequencyInTime = normalizedSuccessFrequencyInTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}

