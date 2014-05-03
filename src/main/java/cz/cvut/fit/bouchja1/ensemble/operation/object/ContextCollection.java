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
    private String id;
    private List<String> bandits;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getBandits() {
        return bandits;
    }

    public void setBandits(List<String> bandits) {
        this.bandits = bandits;
    }
    
    
}
