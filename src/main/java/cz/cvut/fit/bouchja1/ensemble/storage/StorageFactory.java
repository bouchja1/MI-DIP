package cz.cvut.fit.bouchja1.ensemble.storage;

import org.springframework.core.env.Environment;

/**
 *
 * @author jan
 */
public class StorageFactory {
    
    public static IStorage getStorage(Environment env) {
        switch (env.getProperty("storage")) {
            case "cassandra" : 
                return new CassandraStorage(env);                
            default : 
                return new JvmStorage();
        }
    }
    
}
