/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.main;

import cz.cvut.fit.bouchja1.ensemble.storage.CassandraStorage;
import cz.cvut.fit.bouchja1.ensemble.spring.ApplicationBean;
import cz.cvut.fit.bouchja1.ensemble.spring.AppConfig;
import cz.cvut.fit.bouchja1.ensemble.api.EnsembleApiFacade;
import cz.cvut.fit.bouchja1.ensemble.api.EnsembleApiFacadeImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author jan
 */
public abstract class EnsembleAppBase {

    protected final Log logger = LogFactory.getLog(getClass());
    private ApplicationBean applicationBean;
    private EnsembleApiFacade api;

    public void loadConsoleApplication() {
        try {
            logger.info("Start with loading application.");
            initializeApplication();            
            connectStorage();
            loadEnsembleApi();
            runApplication();
            logger.info("Application was loaded successfully.");
        } catch (Exception ex) {
            handleStartupException("error initialzing application", ex);   
        }
    }

    private void initializeApplication() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        applicationBean = (ApplicationBean) context.getBean("applicationBean");
    }

    private void connectStorage() {
        applicationBean.connectStorage();
    } 

    private void loadEnsembleApi() {
        api = new EnsembleApiFacadeImpl();
    }

    private void runApplication() {
        applicationBean.run(api);
    }

    private void handleStartupException(String message, Exception ex) {
        logger.error(message, ex);
    }
}
