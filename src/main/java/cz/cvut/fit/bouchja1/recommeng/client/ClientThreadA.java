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
public class ClientThreadA extends Thread {

    private String name;
    private Communication communication;

    public ClientThreadA(String name, Communication communication) {
        this.name = name;
        this.communication = communication;
    }

    public void run() {                
        //ALGORITHMS
        communication.getRandomRecommendation();
        //communication.getLatestRecommendation();
        //communication.getTopratedRecommendation();
        //communication.getCfUserRecommendation();
        //communication.getCfItemRecommendation();
        //communication.getMltRecommendation();
        
        //communication.sendUserRatingItemFeedback();
        
        //communication.deleteDocumentInCore();
        
        //ENSEMBLE
        //communication.createContextCollectionRest();
        //communication.createBanditSuperCollectionRest();
        //communication.getBanditCollections();
        //communication.getBanditSuperCollections();
        
        //communication.getBestBanditContextCollection();
        //communication.getBestBanditSuperCollection();
        
        //communication.sendUseEnsembleOperation();        
        //communication.sendFeedbackEnsembleOperation();
        //communication.sendSuperFeedbackEnsembleOperation();
        
        
    }
}
