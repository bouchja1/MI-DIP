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
public class ClientThreadF extends Thread {

    private String name;
    private Communication communication;

    public ClientThreadF(String name, Communication communication) {
        this.name = name;
        this.communication = communication;
    }

    public void run() {

    }
}
