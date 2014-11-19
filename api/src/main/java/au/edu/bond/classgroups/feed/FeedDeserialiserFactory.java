package au.edu.bond.classgroups.feed;

import au.edu.bond.classgroups.config.ConfigurationService;
import au.edu.bond.classgroups.feed.csv.FileCsvFeedDeserialiser;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Shane Argo on 13/06/2014.
 */
public class FeedDeserialiserFactory {

    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private ConfigurationService configurationService;

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

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
