package cz.cvut.bouchja1.ensemble.spring;

import cz.cvut.bouchja1.ensemble.storage.CassandraStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author jan
 */
@Component
public class ScheduledJob {

    private final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private ApplicationBean application;
    
    //http://javahunter.wordpress.com/2011/05/05/cronscheduler-in-spring/
    @Scheduled(cron = "${scheduling.job.cron}")
    public void run() {
        /*
        logger.info("Scheduled job to persist current application job started.");
        application.saveCurrentState();
        */ 
    }
}
