package au.edu.bond.classgroups.stripes;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.config.FeedHeaderConfig;
import au.edu.bond.classgroups.dao.BbAvailableGroupToolDAO;
import au.edu.bond.classgroups.model.Schedule;
import au.edu.bond.classgroups.util.ServerUtil;
import blackboard.data.navigation.NavigationApplication;
import blackboard.persist.PersistenceException;
import com.alltheducks.bb.stripes.EntitlementRestrictions;
import com.alltheducks.configutils.service.ConfigurationService;
import com.google.gson.Gson;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.integration.spring.SpringBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shane Argo on 3/06/2014.
 */
@EntitlementRestrictions(entitlements={"bond.classgroups.admin.MODIFY"}, errorPage="/noaccess.jsp")
public class ConfigAction implements ActionBean {

    private ActionBeanContext context;

    private Configuration configuration;
    private ConfigurationService<Configuration> configurationService;
    private BbAvailableGroupToolDAO bbAvailableGroupToolDAO;

    private String currentServerName;

    @DefaultHandler
    public Resolution display() {
        currentServerName = ServerUtil.getServerName();
        return new ForwardResolution("/WEB-INF/jsp/config.jsp");
    }

    public Resolution save() {
        final ArrayList<Schedule> cleanSchedules = new ArrayList<Schedule>();
        if(configuration.getSchedules() != null) {
            for (Schedule schedule : configuration.getSchedules()) {
                if (schedule != null) {
                    cleanSchedules.add(schedule);
                }
            }
        }
        configuration.setSchedules(cleanSchedules);

        configurationService.persistConfiguration(configuration);

        return new RedirectResolution("/Index.action");
    }

    public String getSchedulesJson() {
        Gson gson = new Gson();
        return gson.toJson(configuration.getSchedules());
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void prepare() {
        configuration = configurationService.loadConfiguration();
        if (configuration == null) {
            configuration = new Configuration();
        }
        if (configuration.getProcessingThreads() <= 0) {
            configuration.setProcessingThreads(1);
        }
        if(configuration.getQueuePollingFrequencySeconds() <= 0) {
            configuration.setQueuePollingFrequencySeconds(10);
        }
        if(configuration.getFeedHeaderConfig() == null) {
            final FeedHeaderConfig feedHeaderConfig = new FeedHeaderConfig();
            feedHeaderConfig.setCourseIdHeader("courseId");
            feedHeaderConfig.setGroupIdHeader("groupId");
            feedHeaderConfig.setTitleHeader("title");
            feedHeaderConfig.setLeaderHeader("leader");
            feedHeaderConfig.setGroupSetHeader("groupSet");
            feedHeaderConfig.setAvailableHeader("available");
            feedHeaderConfig.setToolsHeader("tools");
            feedHeaderConfig.setUserIdHeader("userId");
            configuration.setFeedHeaderConfig(feedHeaderConfig);
        }
    }

    @Before(on = "save", stages = LifecycleStage.BindingAndValidation)
    public void clearScheduleList() {
        if(configuration != null) {
            final List<Schedule> schedules = configuration.getSchedules();
            if (schedules != null) {
                schedules.clear();
            }
            final List<String> defaultTools = configuration.getDefaultTools();
            if(defaultTools != null) {
                defaultTools.clear();
            }
        }
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @SpringBean
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public ConfigurationService<Configuration> getConfigurationService() {
        return configurationService;
    }

    @SpringBean
    public void setConfigurationService(ConfigurationService<Configuration> configurationService) {
        this.configurationService = configurationService;
    }

    public BbAvailableGroupToolDAO getBbAvailableGroupToolDAO() {
        return bbAvailableGroupToolDAO;
    }

    @SpringBean
    public void setBbAvailableGroupToolDAO(BbAvailableGroupToolDAO bbAvailableGroupToolDAO) {
        this.bbAvailableGroupToolDAO = bbAvailableGroupToolDAO;
    }

    public String getCurrentServerName() {
        return currentServerName;
    }

    public void setCurrentServerName(String currentServerName) {
        this.currentServerName = currentServerName;
    }

    public Map<String, String> getAllGroupTools() throws PersistenceException {
        Map<String, String> result = new HashMap<String, String>();
        for(NavigationApplication app : bbAvailableGroupToolDAO.getAllGroupTools()) {
            result.put(app.getApplication(), app.getLabel());
        }
        return result;
    }

}
