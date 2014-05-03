/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditCollection;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditSuperCollection;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.EnsembleOperation;
import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public interface EnsembleEndpoint {
    public Response createBanditCollection(BanditCollection banditCollection); 
    public Response createBanditSuperCollection(BanditSuperCollection banditSuperCollection);
    public Response getBanditCollections();
    public Response getBanditSuperCollections();
    public Response getBestBanditCollection(String collectionId, String filter);
    public Response getBestBanditSuperCollection(String supercollectionId);
    public Response sendEnsembleOperation(String collectionId, String banditId, EnsembleOperation ensembleOperation);    
}
