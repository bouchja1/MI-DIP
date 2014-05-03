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
public class Supercollection {
    private String id;
    private List<ContextCollection> contextCollections;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ContextCollection> getContextCollections() {
        return contextCollections;
    }

    public void setContextCollections(List<ContextCollection> contextCollections) {
        this.contextCollections = contextCollections;
    }
    
    
}
