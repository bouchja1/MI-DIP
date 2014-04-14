/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dpr.rest.client.service;

import cz.cvut.fit.bouchja1.mi_dpr.rest.client.endpoint.AlgorithmEndpoint;
import cz.cvut.fit.bouchja1.mi_dpr.rest.client.endpoint.CoresEndpoint;
import cz.cvut.fit.bouchja1.mi_dpr.rest.client.endpoint.EnsembleEndpoint;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
* Registers the components to be used by the JAX-RS application  
* http://www.codingpedia.org/ama/restful-web-services-example-in-java-with-jersey-spring-and-mybatis/
*
* @author jan
*
*/
public class RecommengApplication extends ResourceConfig {
        /**
    * Register JAX-RS application components.
    */    
    public RecommengApplication(){
        register(RequestContextFilter.class);
        //register(AlgorithmEndpoint.class);
        register(CoresEndpoint.class);
        //register(EnsembleEndpoint.class);
        register(JacksonFeature.class);        
    }
}
