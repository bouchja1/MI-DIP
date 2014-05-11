/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.run;

/**
 *
 * @author jan
 */
public class AbstractTest {
    protected static final String ensembleLocation = "tcp://127.0.0.1:5555";
    protected static final String serverLocation = "http://localhost:8089/";        
    protected static final String restfulApiLocation = serverLocation + "ensembleRestApi/";        
    protected static final String articleCore = "solr/articleCore";
    protected static final String behavioralCore = "solr/behavioralCore";
}
