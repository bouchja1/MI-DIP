/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.operation;

import cz.cvut.fit.bouchja1.ensemble.api.EnsembleApiFacade;
import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jan
 */
public class OperationCreateBanditCollection extends AbstractOperation {
        
    private String banditCollectionId;
    private Set<String> banditIds;

    @Override
    public Reply executeOperation(EnsembleApiFacade api) {        
        return api.createBanditSet(banditCollectionId, banditIds);
    }

    @Override
    public boolean validateOperation() {
        if ((banditCollectionId == null) || (banditIds.isEmpty())) {
            errorMessage = "You need to pass all parameters (empty collection ID or algorithms IDs.)";
            return false;
        }
        return true;
    }

    @Override
    public void parseParameters(Map<String, String> parameters) {
        this.banditCollectionId = parameters.get("collectionId");
        String[] bandits = parameters.get("bandits").split(",");
        this.banditIds = new HashSet<>(Arrays.asList(bandits));
    }
    
}
