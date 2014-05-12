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
@XmlRootElement
public class EnsembleOperation implements Serializable {

    private static final long serialVersionUID = -4039185696075322053L;
    
    private int bandit;
    private String operation;
    private String feedbackType;

    public EnsembleOperation() {
    }

    @XmlElement
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @XmlElement
    public int getBandit() {
        return bandit;
    }

    @XmlElement(required = false) 
    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    
    public void setBandit(int bandit) {
        this.bandit = bandit;
    }        
}
