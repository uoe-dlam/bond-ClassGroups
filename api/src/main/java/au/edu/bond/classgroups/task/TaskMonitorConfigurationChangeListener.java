package au.edu.bond.classgroups.task;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.feed.FeedDeserialiserFactory;
import au.edu.bond.classgroups.feed.csv.FileCsvFeedDeserialiser;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.logging.TaskLoggerFactory;
import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.DirectoryFactory;
import au.edu.bond.classgroups.service.TaskService;
import com.alltheducks.configutils.monitor.ConfigurationChangeListener;

import java.util.concurrent.*;

/**
 * Created by shane on 9/09/2015.
 */
public class TaskMonitorConfigurationChangeListener implements ConfigurationChangeListener<Configuration>, AutoCloseable {

    public static final long SHUTDOWN_TIMEOUT_MILLISECONDS = 15000;

    private TaskExecutor taskExecutor;
    private TaskService taskService;
    private TaskProcessorFactory taskProcessorFactory;
    private TaskLoggerFactory taskLoggerFactory;
    private FeedDeserialiserFactory feedDeserialiserFactory;
    private DirectoryFactory directoryFactory;

    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;

    public TaskMonitorConfigurationChangeListener() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configurationChanged(Configuration configuration) {
        if(scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        scheduledFuture = executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Task task = null;
                try {
                    task = taskService.getNextPending();
                    if (task != null) {
                        final TaskLogger taskLogger = taskLoggerFactory.getLogger(task);

                        final FileCsvFeedDeserialiser fileCsvFeedDeserialiser = feedDeserialiserFactory.getFileCsvFeedDeserialiser();
                        fileCsvFeedDeserialiser.setGroupsFile(directoryFactory.getFeedGroupsFile(task));
                        fileCsvFeedDeserialiser.setMembersFile(directoryFactory.getFeedMembersFile(task));

                        final TaskProcessor taskProcessor = taskProcessorFactory.getDefault();
                        taskProcessor.setTask(task);
                        taskProcessor.setTaskLogger(taskLogger);
                        taskProcessor.setFeedDeserialiser(fileCsvFeedDeserialiser);

                        taskExecutor.executeTaskProcessor(taskProcessor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

                , 0, 10, TimeUnit.SECONDS);
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

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public TaskProcessorFactory getTaskProcessorFactory() {
        return taskProcessorFactory;
    }

    public void setTaskProcessorFactory(TaskProcessorFactory taskProcessorFactory) {
        this.taskProcessorFactory = taskProcessorFactory;
    }

    public TaskLoggerFactory getTaskLoggerFactory() {
        return taskLoggerFactory;
    }

    public void setTaskLoggerFactory(TaskLoggerFactory taskLoggerFactory) {
        this.taskLoggerFactory = taskLoggerFactory;
    }

    public FeedDeserialiserFactory getFeedDeserialiserFactory() {
        return feedDeserialiserFactory;
    }

    public void setFeedDeserialiserFactory(FeedDeserialiserFactory feedDeserialiserFactory) {
        this.feedDeserialiserFactory = feedDeserialiserFactory;
    }

    public DirectoryFactory getDirectoryFactory() {
        return directoryFactory;
    }

    public void setDirectoryFactory(DirectoryFactory directoryFactory) {
        this.directoryFactory = directoryFactory;
    }
}
