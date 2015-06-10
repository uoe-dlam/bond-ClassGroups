package au.edu.bond.classgroups.manager;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.model.Schedule;
import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.TaskService;
import au.edu.bond.classgroups.task.ScheduledTaskProcessor;
import au.edu.bond.classgroups.task.TaskCleanupRunner;
import com.alltheducks.configutils.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by Shane Argo on 8/07/2014.
 */
public class ScheduleManager implements Closeable {

    public static final long SHUTDOWN_TIMEOUT_MILLISECONDS = 30000;

    @Autowired
    private ConfigurationService<Configuration> configurationService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ScheduledTaskProcessor scheduledTaskProcessor;

    @Autowired
    private TaskCleanupRunner taskCleanupRunner;
    @Autowired
    private Long cleanupDelaySeconds;
    @Autowired
    private Long cleanupPeriodSeconds;


    public ScheduleManager() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    private Task currentlyScheduledTask;
    private Set<ScheduledFuture<?>> scheduledFutureSet = new HashSet<ScheduledFuture<?>>();
    private ScheduledExecutorService scheduledExecutorService;

    private ScheduledFuture<?> cleanupScheduledFuture;

    public void updateSchedules() {
        final Configuration configuration = configurationService.loadConfiguration();
        updateTaskSchedules(configuration);
        updateCleanupSchedule(configuration);
    }

    private void updateTaskSchedules(Configuration configuration) {
        final List<Schedule> schedules = configuration.getSchedules();

        Task task = null;
        task = taskService.updateSchedule(configuration.isSchedulesEnabled(), schedules);
        if (currentlyScheduledTask != null && currentlyScheduledTask.equals(task)) {
            return;
        }

        cancelTaskSchedules();

        if(task == null) {
            return;
        }

        Date now = new Date();
        long diff = task.getScheduledDate().getTime() - now.getTime();

        scheduledTaskProcessor.setTask(task);
        final ScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(scheduledTaskProcessor, diff, TimeUnit.MILLISECONDS);
        scheduledFutureSet.add(scheduledFuture);

        currentlyScheduledTask = task;
    }

    private void updateCleanupSchedule(Configuration configuration) {
        if(cleanupScheduledFuture != null) {
            cleanupScheduledFuture.cancel(false);
        }

        if(configuration.isAutoCleanUpOldTasks()) {
            cleanupScheduledFuture = scheduledExecutorService.scheduleAtFixedRate(taskCleanupRunner, cleanupDelaySeconds, cleanupPeriodSeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public void close() throws IOException {
        cancelTaskSchedules();

        if(scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();

            boolean success = false;
            try {
                success = scheduledExecutorService.awaitTermination(SHUTDOWN_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!success) {
                System.out.println("Groups task failed to complete processing before termination.");
            }
        }
    }

    private void cancelTaskSchedules() {
        for(ScheduledFuture<?> scheduledFuture : scheduledFutureSet) {
            scheduledFuture.cancel(false);
            scheduledFutureSet.remove(scheduledFuture);
        }
    }

    public ConfigurationService<Configuration> getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService<Configuration> configurationService) {
        this.configurationService = configurationService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
}
