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
public class OperationCreateBanditSuperCollection extends AbstractOperation {
        
    private String banditSuperCollectionId;
    private Set<String> collectionIds;

    @Override
    public Reply executeOperation(EnsembleApiFacade api) {        
        return api.createBanditSuperSet(banditSuperCollectionId, collectionIds);
    }

    @Override
    public boolean validateOperation() {
        if ((banditSuperCollectionId == null) || (collectionIds.isEmpty())) {
            errorMessage = "You need to pass all parameters (empty supercollection ID, collection IDs)";
            return false;
        }
        return true;
    }

    @Override
    public void parseParameters(Map<String, String> parameters) {
        this.banditSuperCollectionId = parameters.get("supercollectionId");
        String[] collections = parameters.get("collections").split(",");
        this.collectionIds = new HashSet<>(Arrays.asList(collections));
    }
    
}
