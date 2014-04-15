/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jan
 */
@XmlRootElement(name = "user")
public class UserIdDocument {

    private Integer id;

    public UserIdDocument() {
        // required for JAXB
    }

    public UserIdDocument(Integer id) {
        this.id = id;
    }

    @XmlAttribute
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
