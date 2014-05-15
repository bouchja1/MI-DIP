/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jan
 */
@XmlRootElement(name="collection")
public class CreatedCollection {
    private String id;

    public CreatedCollection() {
    }

    public CreatedCollection(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
}
