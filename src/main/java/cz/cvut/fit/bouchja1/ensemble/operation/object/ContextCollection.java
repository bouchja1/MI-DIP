/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.operation.object;

import java.util.List;

/**
 *
 * @author jan
 */
public class ContextCollection {
    private int id;
    private String name;
    private List<Integer> bandits;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getBandits() {
        return bandits;
    }

    public void setBandits(List<Integer> bandits) {
        this.bandits = bandits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}
