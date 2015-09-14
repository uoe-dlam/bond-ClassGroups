package au.edu.bond.classgroups.stripes;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.exception.InvalidTaskStateException;
import au.edu.bond.classgroups.feed.FeedFetcher;
import au.edu.bond.classgroups.feed.FeedFetcherFactory;
import au.edu.bond.classgroups.logging.TaskLoggerFactory;
import au.edu.bond.classgroups.task.TaskExecutor;
import au.edu.bond.classgroups.task.TaskProcessor;
import au.edu.bond.classgroups.task.TaskProcessorFactory;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.TaskService;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.integration.spring.SpringBean;

import java.io.IOException;

/**
 * Created by Shane Argo on 3/06/2014.
 */
public class DefaultDeserialiserAction implements ActionBean {

    private ActionBeanContext context;

    private boolean defaultDeserialiserConfigured;

    private TaskService taskService;
    private TaskExecutor taskExecutor;
    private String pluginsUrl;
    private Configuration configuration;
    private TaskLoggerFactory taskLoggerFactory;
    private FeedFetcherFactory feedFetcherFactory;


    @DefaultHandler
    public Resolution display() {
        defaultDeserialiserConfigured = (configuration.getFeedFetcherType() != null);

        return new ForwardResolution("/WEB-INF/jsp/defaultdeserialiser.jsp");
    }

    public Resolution execute() throws IOException, InvalidTaskStateException {
        Task task = null;
        TaskLogger taskLogger = null;

        try {
            task = taskService.createTask();
            taskLogger = taskLoggerFactory.getLogger(task);

            if (task.getStatus() == Task.Status.SKIPPED) {
                taskLogger.info("bond.classgroups.info.skipping");
                return null;
            }

            final FeedFetcher configuredFeedFetcher = feedFetcherFactory.getConfiguredFeedFetcher();
            configuredFeedFetcher.fetchData(task);

            taskService.pendingTask(task);

        } catch (Exception e) {
            if(task != null) {
                e.printStackTrace();
                if(taskLogger != null) {
                    taskLogger.error("bond.classgroups.error.defaultexecutorfailed", e);
                }
                taskService.failTask(task);
            }
        }

        if(task != null) {
            return new RedirectResolution(String.format("/Log.action?taskId=%s", task.getId()));
        }
        return null;

    }

    public boolean isDefaultDeserialiserConfigured() {
        return defaultDeserialiserConfigured;
    }

    public void setDefaultDeserialiserConfigured(boolean defaultDeserialiserConfigured) {
        this.defaultDeserialiserConfigured = defaultDeserialiserConfigured;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    @SpringBean
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    @SpringBean
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public String getPluginsUrl() {
        return pluginsUrl;
    }

    @SpringBean
    public void setPluginsUrl(String pluginsUrl) {
        this.pluginsUrl = pluginsUrl;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @SpringBean
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public TaskLoggerFactory getTaskLoggerFactory() {
        return taskLoggerFactory;
    }

    @SpringBean
    public void setTaskLoggerFactory(TaskLoggerFactory taskLoggerFactory) {
        this.taskLoggerFactory = taskLoggerFactory;
    }

    public FeedFetcherFactory getFeedFetcherFactory() {
        return feedFetcherFactory;
    }

    @SpringBean
    public void setFeedFetcherFactory(FeedFetcherFactory feedFetcherFactory) {
        this.feedFetcherFactory = feedFetcherFactory;
    }
}
