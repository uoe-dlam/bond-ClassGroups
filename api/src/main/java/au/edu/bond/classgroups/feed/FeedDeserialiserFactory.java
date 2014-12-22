package au.edu.bond.classgroups.feed;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.feed.csv.FileCsvFeedDeserialiser;
import com.alltheducks.configutils.service.ConfigurationService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Shane Argo on 13/06/2014.
 */
public class FeedDeserialiserFactory {

    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private ConfigurationService<Configuration> configurationService;

    public FeedDeserialiser getDefault() {
        final String defaultFeedDeserialiserBean = configurationService.loadConfiguration().getDefaultFeedDeserialiserBean();
        if(defaultFeedDeserialiserBean == null) {
            return null;
        }
        return beanFactory.getBean(defaultFeedDeserialiserBean, FeedDeserialiser.class);
    }

    public FeedDeserialiser getByBeanId(String beanId) {
        return beanFactory.getBean(beanId, FeedDeserialiser.class);
    }

    public FileCsvFeedDeserialiser getHttpPushCsvFeedDeserialiser() {
        return beanFactory.getBean("httpPushCsvFeedDeserialiser", FileCsvFeedDeserialiser.class);
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public ConfigurationService<Configuration> getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService<Configuration> configurationService) {
        this.configurationService = configurationService;
    }
}
