/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.rest;

import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author jan
 */
public interface EnsembleEndpoint {
    public Response createBanditSet(String banditSetId, List<String> banditIds);
    public Response detectBestBandit(String banditCollectionId);
    public Response requestBanditRecommendation(String banditCollectionId, String banditId);
    public Response responseMsg(String parameter, String value);
}
