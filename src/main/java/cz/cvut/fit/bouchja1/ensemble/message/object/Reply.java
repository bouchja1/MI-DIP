/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.message.object;

import cz.cvut.fit.bouchja1.ensemble.operation.object.ContextCollection;
import cz.cvut.fit.bouchja1.ensemble.operation.object.Supercollection;
import java.util.List;

/**
 *
 * @author jan
 */
public class Reply {
    private String status;
    private String message;
    private List<ContextCollection> contextCollection;
    private List<Supercollection> supercollection;

    public Reply(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

}
