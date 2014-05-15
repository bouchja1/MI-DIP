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
@XmlRootElement(name = "message")
public class Message {

    private String message;

    public Message() {
        // required for JAXB
    }

    public Message(String message) {
        this.message = message;
    }

    @XmlAttribute
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
}
