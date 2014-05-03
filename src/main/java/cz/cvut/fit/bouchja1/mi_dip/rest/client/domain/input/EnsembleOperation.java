/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jan
 */
@XmlRootElement(name = "banditCollection")
public class EnsembleOperation implements Serializable {

    private static final long serialVersionUID = -4039185696075322053L;
    private String operation;
    private int value;

    public EnsembleOperation() {
    }

    @XmlElement
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @XmlElement(required = false)    
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    
}
