/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.api;

import cz.cvut.fit.bouchja1.client.request.EnsembleRequest;
import cz.cvut.fit.bouchja1.client.request.RestfulRequest;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;

/**
 *
 * @author jan
 */
public class EnsembleClient {
    private EnsembleRequest ensembleRequest;
    private RestfulRequest restfulRequest;
    private Client client;    
    private String ensembleLocation;
    private String restfulApiLocation;

    public EnsembleClient(String ensembleLocation, String restfulApiLocation) {
        this.ensembleLocation = ensembleLocation;
        this.restfulApiLocation = restfulApiLocation;
        invokeAndRegisterClient();
    }      
    
    private void invokeAndRegisterClient() {
        ClientConfig clientConfig = new ClientConfig()
                .register(JsonProcessingFeature.class)
                .property(JsonGenerator.PRETTY_PRINTING, true);
        client = ClientBuilder.newClient(clientConfig);        
    }    

    public Client getClient() {
        return client;
    }        

    public EnsembleRequest getEnsembleRequest() {
        return ensembleRequest;
    }

    public void setEnsembleRequest(EnsembleRequest ensembleRequest) {
        this.ensembleRequest = ensembleRequest;
    }

    public RestfulRequest getRestfulRequest() {
        return restfulRequest;
    }

    public void setRestfulRequest(RestfulRequest restfulRequest) {
        this.restfulRequest = restfulRequest;
    }        

    public String getEnsembleLocation() {
        return ensembleLocation;
    }

    public String getRestfulApiLocation() {
        return restfulApiLocation;
    }       
}
