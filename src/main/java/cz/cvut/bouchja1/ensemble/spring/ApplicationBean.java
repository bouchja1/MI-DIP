/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.bouchja1.ensemble.spring;

import cz.cvut.bouchja1.ensemble.storage.IStorage;
import cz.cvut.bouchja1.ensemble.storage.StorageFactory;
import cz.cvut.fit.bouchja1.ensemble.api.EnsembleApiFacade;
import cz.cvut.fit.bouchja1.ensemble.bandits.BanditsMachine;
import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.socket.MultiThreadServer;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

/**
 *
 * @author jan
 */
@Scope("singleton")
public class ApplicationBean {

    private final Log logger = LogFactory.getLog(getClass());
    private List<BayesianStrategy> strategies;
    private IStorage storage;
    private Environment env;

    public ApplicationBean(Environment env) {
        this.env = env;
    }

    public void run(EnsembleApiFacade api) {
        /*
         * PRO SIMULACI - POKUSY ZDE?
         */
        strategies = getLastBanditConfiguration();       
        api.setStrategies(strategies); //muze by prazdne  
        api.setStorage(storage);
        api.setEnvironment(env);

        //http://sysgears.com/articles/load-balancing-work-between-java-threads-using-zeromq/
        MultiThreadServer server = new MultiThreadServer(env.getProperty("zeromq.host"), env.getProperty("zeromq.port"));
        server.run(api);
    }

    /*
     * Method called by cron to persist current state
     */
    public void saveCurrentState() {
        try {
            storage.saveCurrentState(strategies);
        } catch (NullPointerException ex) {
            logger.error("Maybe application is not initialized yet.", ex);
        }
    }

    public void connectStorage() {
        //instanciovat to podle toho, co prijde z konfigurace
        storage = StorageFactory.getStorage(env);
    }
    
    /*
     * bude se sahat do databaze a nacitat posledni ulozena konfigurace napr. pred padem aplikace
     * sada banditu se bude specifikovat v konfiguraku, pokud nebude specifikovana nebo bude neexistujici, nacte se nejaka defaultni (prvni ulozena)
     * Pokud zadna takova nebo ani defaultni neexistuje, nenacte se nic a bude to muset byt osetrene
     */
    private List<BayesianStrategy> getLastBanditConfiguration() {        
        return storage.loadLastConfiguration(env);
    }    
}
