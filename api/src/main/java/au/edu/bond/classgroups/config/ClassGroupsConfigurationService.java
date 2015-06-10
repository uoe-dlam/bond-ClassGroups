package au.edu.bond.classgroups.config;

import com.alltheducks.configutils.service.CachingConfigurationService;
import com.alltheducks.configutils.service.ConfigurationService;

/**
 * Created by Shane Argo on 21/12/14.
 */
public class ClassGroupsConfigurationService extends CachingConfigurationService<Configuration> {

    public ClassGroupsConfigurationService(ConfigurationService<Configuration> internalConfigurationService) {
        super(internalConfigurationService);
    }

    @Override
    public Configuration loadConfiguration() {
        return super.loadConfiguration();
    }
}
