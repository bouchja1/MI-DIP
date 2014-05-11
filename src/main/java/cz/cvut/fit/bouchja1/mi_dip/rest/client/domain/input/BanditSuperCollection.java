/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input;

import java.io.Serializable;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class BanditSuperCollection implements Serializable {
    private static final long serialVersionUID = -4039686696075339053L;
    
    private String name;
    private Set<Integer> contextCollections;

    public BanditSuperCollection() {
    }

        @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

        @XmlElement
    public Set<Integer> getContextCollections() {
        return contextCollections;
    }

    public void setContextCollections(Set<Integer> contextCollections) {
        this.contextCollections = contextCollections;
    }

    
    
}
