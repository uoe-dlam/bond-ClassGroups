package au.edu.bond.classgroups.stripes;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.service.TaskService;
import com.alltheducks.bb.stripes.EntitlementRestrictions;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.integration.spring.SpringBean;

import java.io.IOException;

/**
 * Created by Shane Argo on 3/06/2014.
 */
@EntitlementRestrictions(entitlements={"bond.classgroups.admin.MODIFY"}, errorPage="/noaccess.jsp")
public class CleanUpAction implements ActionBean {

    private ActionBeanContext context;

    private TaskService taskService;
    private Configuration configuration;
    private String pluginsUrl;

    @DefaultHandler
    public Resolution display() {
        return new ForwardResolution("/WEB-INF/jsp/cleanup.jsp");
    }

    public Resolution execute() throws IOException {
        taskService.deleteOlderThan(configuration.getCleanUpDaysToKeep());
        return new RedirectResolution("/TaskList.action");
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    public String getPluginsUrl() {
        return pluginsUrl;
    }

    @SpringBean
    public void setPluginsUrl(String pluginsUrl) {
        this.pluginsUrl = pluginsUrl;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    @SpringBean
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @SpringBean
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
