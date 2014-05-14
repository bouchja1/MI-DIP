package cz.cvut.fit.bouchja1.ensemble.operation.object;

import java.util.List;

/**
 *
 * @author jan
 */
public class Supercollection {
    private int id;
    private String name;
    private List<ContextCollection> contextCollections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ContextCollection> getContextCollections() {
        return contextCollections;
    }

    public void setContextCollections(List<ContextCollection> contextCollections) {
        this.contextCollections = contextCollections;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}
