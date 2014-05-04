package cz.cvut.fit.bouchja1.recommeng.client;

import cz.cvut.fit.bouchja1.client.api.EnsembleClientApi;

/**
 * Hello world!
 *
 */
public class App {
    //https://jersey.java.net/documentation/latest/client.html
    //https://jersey.java.net/documentation/2.7/user-guide.html#json.jackson
    //https://jersey.java.net/documentation/2.7/user-guide.html#json.jettison
    
    private static final String ensembleLocation = "tcp://127.0.0.1:5555";
    private static final String restfulApiLocation = "http://localhost:8089/ensembleRestApi/";    

    public static void main(String[] args) {        
        EnsembleClientApi clientApi = new EnsembleClientApi(ensembleLocation, restfulApiLocation);
                
        (new ClientThread("Náročný klient", clientApi)).start();
    }
}
