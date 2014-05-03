/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jan
 */
@XmlRootElement(name = "bandit")
public class BanditId {

    private String id;

    public BanditId() {
        // required for JAXB
    }

    public BanditId(String id) {
        this.id = id;
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }    
}
