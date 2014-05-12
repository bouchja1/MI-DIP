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
public interface EnsembleEndpointSupercollection {       
    public Response createBanditSuperCollection(BanditSuperCollection banditSuperCollection);
    public Response getBanditSuperCollections();
    public Response getBestBanditSuperCollection(String supercollectionId, String filter);
    public Response sendEnsembleOperationToSuperCollection(String collectionId, EnsembleOperation ensembleOperation);      
}
