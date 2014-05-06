/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.message.object;

import cz.cvut.fit.bouchja1.ensemble.operation.object.ContextCollection;
import cz.cvut.fit.bouchja1.ensemble.operation.object.Supercollection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jan
 */
public class Reply {
    private int status;
    private String message;
    private int bestBandit = -1;
    private int collection = -1;
    private List<ContextCollection> contextCollection = new ArrayList<>();
    private List<Supercollection> supercollection = new ArrayList<>();

    public Reply(int status, String message, int bestBandit, int collection) {
        this.status = status;
        this.message = message;
        this.bestBandit = bestBandit;
        this.collection = collection;
    }

    public Reply(int status, String message) {
        this.status = status;
        this.message = message;
    }    
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ContextCollection> getContextCollection() {
        return contextCollection;
    }

    public void setContextCollection(List<ContextCollection> contextCollection) {
        this.contextCollection = contextCollection;
    }

    public List<Supercollection> getSupercollection() {
        return supercollection;
    }

    public void setSupercollection(List<Supercollection> supercollection) {
        this.supercollection = supercollection;
    }

    public int getBestBandit() {
        return bestBandit;
    }

    public void setBestBandit(int bestBandit) {
        this.bestBandit = bestBandit;
    }

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }


    
}
