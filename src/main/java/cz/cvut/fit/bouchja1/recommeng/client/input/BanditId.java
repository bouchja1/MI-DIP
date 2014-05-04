/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.recommeng.client.input;

/**
 *
 * @author jan
 */
public class BanditId {
    private String id;

    public BanditId() {
        // required for JAXB
    }

    public BanditId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }       
}
