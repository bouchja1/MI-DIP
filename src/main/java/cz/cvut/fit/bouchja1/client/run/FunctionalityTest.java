/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.run;

import cz.cvut.fit.bouchja1.client.api.Communication;
import cz.cvut.fit.bouchja1.client.api.EnsembleClient;
import static cz.cvut.fit.bouchja1.client.run.AbstractTest.ensembleLocation;
import cz.cvut.fit.bouchja1.client.threads.ClientFunctionalityTest;
import cz.cvut.fit.bouchja1.client.tools.EnvironmentBuilder;
import java.io.IOException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

/**
 *
 * @author jan
 * 
 * Testovani, zda funguji nektere konkretni endpointy
 * - vypis podporovanych algoritmu
 * - vypis podporovanych jader
 * - doporucovani jednotlivymi algoritmy
 */
public class FunctionalityTest extends AbstractTest {

    public static void main(String[] args) throws InterruptedException {        
        EnsembleClient clientApi = new EnsembleClient(ensembleLocation, restfulApiLocation);                       
        
        //NAPLNENI INDEXU NAHODNYMI DATY
        /*
        EnvironmentBuilder environmentBuilder = new EnvironmentBuilder(new Communication(clientApi));       
        environmentBuilder.fillIndexWithTestData(serverLocation, articleCore, behavioralCore);             
          */
        
        (new ClientFunctionalityTest(10, new Communication(clientApi))).start();                                       
    }    
}
