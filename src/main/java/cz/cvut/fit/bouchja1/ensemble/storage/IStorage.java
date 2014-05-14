package cz.cvut.fit.bouchja1.ensemble.storage;

import cz.cvut.fit.bouchja1.ensemble.operation.object.LastEnsembleConfiguration;
import java.util.Set;
import org.springframework.core.env.Environment;

/**
 *
 * @author jan
 */
public interface IStorage {

    public void saveCurrentState(LastEnsembleConfiguration strategies);

    public LastEnsembleConfiguration loadLastConfiguration(Environment env);
    public void createBanditSet(int banditSetId, String banditSetName, Set<String> banditIds);
    public void createBanditSuperSet(int banditSuperSetId, String banditSuperSetName, Set<Integer> collectionIds);
    
}
