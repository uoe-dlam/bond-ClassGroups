package au.edu.bond.classgroups;

import au.edu.bond.classgroups.config.ConfigurationMonitor;
import au.edu.bond.classgroups.config.ConfigurationService;
import au.edu.bond.classgroups.manager.ScheduleManager;
import au.edu.bond.classgroups.task.ScheduledTaskRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shane Argo on 08/07/2014.
 */
public class TaskScheduleContextListener implements ServletContextListener {

    private ConfigurationService configurationService;
    private ScheduledTaskRunner scheduledTaskRunner;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        configurationService = springContext.getBean(ConfigurationService.class);
        scheduledTaskRunner = springContext.getBean(ScheduledTaskRunner.class);

        scheduledTaskRunner.run();

        configurationService.registerReloadListener(scheduledTaskRunner);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(scheduledTaskRunner != null) {
            try {
                scheduledTaskRunner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (configurationService != null) {
                configurationService.unregisterReloadListener(scheduledTaskRunner);
            }
        }
    }
}
