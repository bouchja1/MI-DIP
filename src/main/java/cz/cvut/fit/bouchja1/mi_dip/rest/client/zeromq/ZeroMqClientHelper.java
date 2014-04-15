/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.zeromq;

import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

/**
 *
 * @author jan
 */
@Component
public class ZeroMqClientHelper {
    
    private ZMQ.Socket requester;
    private ZMQ.Context context; 

    public String getNeco() {
        return "NECO";
    }

    public String createBanditSet() {                      
        connect();
        String request = "operation=recommend&collectionId=1&bandit=1";
        
        requester.send(request.getBytes(), 0);
        //Block until we receive a response
        //reply is a byte[] containing whatever the REP socket replied with
        byte[] reply = requester.recv(0);
        System.out.println("Received " + new String(reply));        
        
        disconnect();        
        return new String(reply);
    }
    
    public String detectBestBandit() {       
        connect();
        
        String request = "operation=recommend&collectionId=1&bandit=1";
        
        requester.send(request.getBytes(), 0);
        //Block until we receive a response
        //reply is a byte[] containing whatever the REP socket replied with
        byte[] reply = requester.recv(0);
        System.out.println("Received " + new String(reply));               
        
        disconnect();        
        return new String(reply);
    }
    
    public String requestBanditRecommendation() {
        connect();
        
        String request = "operation=recommend&collectionId=1&bandit=1";
        
        requester.send(request.getBytes(), 0);
        //Block until we receive a response
        //reply is a byte[] containing whatever the REP socket replied with
        byte[] reply = requester.recv(0);
        System.out.println("Received " + new String(reply));       
        
        disconnect();        
        return new String(reply);
    }    
    
    private void connect() {
        context = ZMQ.context(1);        
        requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://localhost:5555");    
    }
    
    private void disconnect() {
        requester.close();
        context.term();   
    }
    
}
