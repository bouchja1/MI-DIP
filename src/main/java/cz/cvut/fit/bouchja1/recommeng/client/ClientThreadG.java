/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.recommeng.client;

import cz.cvut.fit.bouchja1.client.api.Communication;

/**
 *
 * @author jan
 */
public class ClientThreadG extends Thread {

    private int id;
    private Communication communication;

    public ClientThreadG(int id, Communication communication) {
        this.id = id;
        this.communication = communication;
    }

    public void run() {
        
    }
}
