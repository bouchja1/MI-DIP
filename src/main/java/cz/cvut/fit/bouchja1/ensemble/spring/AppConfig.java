/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.spring;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author jan
 * http://fahdshariff.blogspot.cz/2012/09/spring-3-javaconfig-loading-properties.html
 */
@Configuration
@EnableScheduling
@ComponentScan(basePackages = {
    "cz.cvut.bouchja1.ensemble.spring"
})
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Autowired
    private Environment env;
    
    @Value("#{'${bandits.names}'.split(',')}")
    private List<String> allowedBanditValues;

    @Bean(name = "applicationBean")
    public ApplicationBean applicationBeanService() {
        return new ApplicationBean(env, allowedBanditValues);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
        properties.setLocation(new ClassPathResource("application.properties"));
        properties.setIgnoreResourceNotFound(false);
        return properties;
    }
    
    @Bean
    public ScheduledJob task() {
    	return new ScheduledJob();
    }    
}
