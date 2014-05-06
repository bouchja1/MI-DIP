package cz.cvut.fit.bouchja1.recommeng.client;

import cz.cvut.fit.bouchja1.client.api.Communication;
import cz.cvut.fit.bouchja1.client.api.EnsembleClientApi;
import cz.cvut.fit.bouchja1.client.tools.EnvironmentBuilder;

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
        /*
        EnvironmentBuilder environmentBuilder = new EnvironmentBuilder(new Communication(clientApi));
        environmentBuilder.build();          
        */
        
        //jeden porad v kuse uspesnej, ostatni nic - jak se to vyvine 
        (new ClientThreadE("Jeden porad uspesnej", new Communication(clientApi))).start();        
        
        //(new ClientThreadA("Spokojený klient", new Communication(clientApi))).start();
        
        /*
        (new ClientThreadB("Náročný klient", new Communication(clientApi))).start();
        //treba ignorovat ensemble - ze nam neco doprouci, ale ja mu 
        (new ClientThreadC("Ignorujici klient", new Communication(clientApi))).start();
        //prinest nejakou anomalii (treba kdyz si nekdo splete, ze se mu to libi s tim, ze nelibi, tak jak moc to zabije ty vahy)
        (new ClientThreadD("Anomalie", new Communication(clientApi))).start();         
        
        //
        // simulace rovnomernyho - vsichni budou doporucovat cca stejne
        // (+ zahrnout zapornou zpetnou vazbu - dalsi graf - jak je ovlivnovan vyvoj
        // pri zpetne vazbe)
        //
        (new ClientThreadF("Rovnoměrný klient", new Communication(clientApi))).start();    
        
        //jeden nechame hodne nabehnout, pak se bude pridavat dalsimu
        (new ClientThreadG("Prvně nadšený klient, pak ne", new Communication(clientApi))).start();    
        */
    }
}
