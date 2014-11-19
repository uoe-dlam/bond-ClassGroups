package au.edu.bond.classgroups.config;

import java.io.Closeable;

/**
 * Created by Shane Argo on 26/06/2014.
 */
public abstract class ConfigurationMonitor implements Runnable, Closeable {

    private ConfigurationService configurationService;

    public ConfigurationMonitor(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
