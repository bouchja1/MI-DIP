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
    private String bestBanditIdent;
    private String collection;
    private String superCollection;
    private List<ContextCollection> contextCollection = new ArrayList<>();
    private List<Supercollection> supercollection = new ArrayList<>();

    public Reply(int status, String message, int bestBandit, String bestBanditIdent, String collection) {
        this.status = status;
        this.message = message;
        this.bestBanditIdent = bestBanditIdent;
        this.bestBandit = bestBandit;
        this.collection = collection;
    }

    public Reply(int status, String message) {
        this.status = status;
        this.message = message;
    }    
    
    public Reply(int status, String message, String collectionId) {
        this.status = status;
        this.message = message;
        this.collection = collectionId;
    }     
    
    public Reply(int status, String message, String collectionId, String collOrSuperColl) {
        this.status = status;
        this.message = message;
        if ("collection".equals(collOrSuperColl)) {
            this.collection = collectionId;
        } else if ("supercollection".equals(collOrSuperColl)) {
            this.superCollection = collectionId;
        }        
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

    public String getBestBanditIdent() {
        return bestBanditIdent;
    }

    public void setBestBanditIdent(String bestBanditIdent) {
        this.bestBanditIdent = bestBanditIdent;
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

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getSuperCollection() {
        return superCollection;
    }

    public void setSuperCollection(String superCollection) {
        this.superCollection = superCollection;
    }




    
}
