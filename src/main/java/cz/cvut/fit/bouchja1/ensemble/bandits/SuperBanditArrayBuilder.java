/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.bandits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SuperBanditArrayBuilder extends ArrayList<Bandit> {

    Map<Integer, Integer> differentBanditsMap = new HashMap<Integer, Integer>();

    public SuperBanditArrayBuilder(Collection<? extends Bandit> c) {
        super(c);
        this.prepareAddAll(c);
    }
    
    public SuperBanditArrayBuilder() {
    }    

    public int getDifferentNamesAmount() {
        return this.differentBanditsMap.size();
    }

    public int getNameAmount(String name) {
        Integer integer = this.differentBanditsMap.get(name);
        return (integer != null) ? integer : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Bandit e) {
        this.prepareAdd(e);
        return super.add(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        super.clear();
        this.differentBanditsMap.clear();
    }
    
    private void prepareAddAll(Collection<? extends Bandit> c) {
        for (Bandit custom : c) {
            this.prepareAdd(custom);
        }
    }    

    private void prepareAdd(Bandit bandit) {
        if (bandit != null) {
            Integer integer = this.differentBanditsMap.get(bandit.getId());
            this.differentBanditsMap.put(bandit.getId(), (integer != null) ? integer + 1 : 1);
        }
    }

    public Map<Integer, Integer> getDifferentBanditsMap() {
        return differentBanditsMap;
    }
    
    
}
