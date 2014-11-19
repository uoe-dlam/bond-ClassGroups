package au.edu.bond.classgroups;

import au.edu.bond.classgroups.config.ConfigurationMonitor;
import au.edu.bond.classgroups.config.ConfigurationService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shane Argo on 25/06/2014.
 */
public class ConfigMonitoringContextListener implements ServletContextListener {

    private ConfigurationMonitor configurationMonitor;

    ExecutorService executorService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initialising configuration monitor.");

        final WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        ConfigurationService configurationService = springContext.getBean(ConfigurationService.class);
        configurationMonitor = configurationService.getMonitor();

        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(configurationMonitor);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Destroying configuration monitor.");

        try {
            if(configurationMonitor != null) {
                configurationMonitor.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to close the configuration watcher.", e);
        }

        if(executorService != null) {
            executorService.shutdownNow();

            boolean terminated;
            try {
                terminated = executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interruption whilst terminating configuration monitor");
            }

            if (!terminated) {
                throw new RuntimeException("Configuration monitor did not terminate within the timeout.");
            }
        }
    }
}
