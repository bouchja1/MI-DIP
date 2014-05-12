/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.endpoint;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.BanditCollection;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input.EnsembleOperation;
import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public interface EnsembleEndpointCollection {
    public Response createBanditCollection(BanditCollection banditCollection); 
    public Response getBanditCollections();
    public Response getBestBanditCollection(String collectionId, String filter); 
    public Response sendEnsembleOperationToCollection(String supercollectionId, EnsembleOperation ensembleOperation);      
}
