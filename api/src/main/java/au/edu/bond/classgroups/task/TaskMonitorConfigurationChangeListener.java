package au.edu.bond.classgroups.task;

import au.edu.bond.classgroups.config.Configuration;
import com.alltheducks.configutils.monitor.ConfigurationChangeListener;

import java.util.concurrent.*;

/**
 * Created by shane on 9/09/2015.
 */
public class TaskMonitorConfigurationChangeListener implements ConfigurationChangeListener<Configuration>, AutoCloseable {

    public static final long SHUTDOWN_TIMEOUT_MILLISECONDS = 15000;

    private TaskExecutor taskExecutor;
    private ScheduledExecutorService executorService;
    ScheduledFuture<?> scheduledFuture;

    public TaskMonitorConfigurationChangeListener() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configurationChanged(Configuration configuration) {
        if(scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        scheduledFuture = executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void close() throws Exception {
        if(executorService != null) {
            executorService.shutdownNow();

            boolean success = false;
            try {
                success = executorService.awaitTermination(SHUTDOWN_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!success) {
                System.out.println("Task monitoring thread failed to complete processing before termination.");
            }
        }
    }
}
