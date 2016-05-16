package au.edu.bond.classgroups.task;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.exception.FeedDeserialisationException;
import au.edu.bond.classgroups.exception.InvalidTaskStateException;
import au.edu.bond.classgroups.feed.FeedDeserialiser;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.manager.GroupManager;
import au.edu.bond.classgroups.manager.SmartViewManager;
import au.edu.bond.classgroups.model.Group;
import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.CacheCleaningService;
import au.edu.bond.classgroups.service.ResourceService;
import au.edu.bond.classgroups.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Shane Argo on 10/06/2014.
 */
public class TaskProcessor implements Runnable {

    public static final int MINIMUM_GROUPS_FOR_MULTITHREADING = 20;

    @Autowired
    private TaskService taskService;
    @Autowired
    private GroupManager groupManager;
    @Autowired
    private SmartViewManager smartViewManager;
    @Autowired
    private Configuration configuration;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private CacheCleaningService cacheCleaningService;

    private Task task;
    private TaskLogger taskLogger;
    private FeedDeserialiser feedDeserialiser;
    private Runnable cleanupRunnable;

    @Override
    public void run() {
        try {
            taskLogger.info("bond.classgroups.info.begin");
            try {
                taskService.beginTask(task);
            } catch (InvalidTaskStateException e) {
                taskLogger.error("bond.classgroups.error.invalidstate", e);
                throw e;
            }

            String logLevelStr;
            switch (configuration.getLoggingLevel()) {
                case NORMAL:
                    logLevelStr = resourceService.getLocalisationString("bond.classgroups.config.logging.logginglevelnormal");
                    break;
                case DEBUG:
                    logLevelStr = resourceService.getLocalisationString("bond.classgroups.config.logging.loggingleveldebug");
                    break;
                default:
                    logLevelStr = resourceService.getLocalisationString("bond.classgroups.config.logging.logginglevelunknown");
            }
            taskLogger.info(resourceService.getLocalisationString("bond.classgroups.info.loglevel", logLevelStr));

            taskLogger.info("bond.classgroups.info.consumingfeed");
            List<Group> groups = null;
            try {
                groups = new ArrayList<Group>(feedDeserialiser.getGroups(taskLogger));
            } catch (FeedDeserialisationException e) {
                taskLogger.error("bond.classgroups.error.faileddeserialise", e);
                throw e;
            }

            Collections.sort(groups, new Comparator<Group>() {
                @Override
                public int compare(Group left, Group right) {
                    return left.getCourseId().compareTo(right.getCourseId());
                }
            });

            final int total = groups.size();
            int memberCount = 0;
            for (Group group : groups) {
                if(group.getMembers() != null) {
                    memberCount += group.getMembers().size();
                }
            }
            taskLogger.info(resourceService.getLocalisationString("bond.classgroups.info.feedconsumed", total, memberCount));
            task.setTotalGroups(total);
            task.setProcessedGroups(0);
            taskService.throttledUpdate(task);

            final ConcurrentLinkedQueue<Group> groupsQueue = new ConcurrentLinkedQueue<Group>(groups);
            taskLogger.info("bond.classgroups.info.updatinggroups");

            int threads = total >= MINIMUM_GROUPS_FOR_MULTITHREADING && configuration.getProcessingThreads() > 0 ? configuration.getProcessingThreads() : 1;
            ExecutorService execService = null;
            final Collection<Future<?>> tasks = new HashSet<>();
            boolean completed = true;
            try {
                execService = Executors.newFixedThreadPool(threads);
                final ArrayBlockingQueue<Boolean> doneQueue = new ArrayBlockingQueue<>(threads);
                for (int i = 0; i < threads; i++) {
                    final Future<?> submit = execService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    final Group group = groupsQueue.poll();
                                    if (group == null) {
                                        doneQueue.put(Boolean.TRUE);
                                        return;
                                    }
                                    handleGroup(group, taskLogger);

                                    task.setProcessedGroups(total - groupsQueue.size());
                                    taskService.throttledUpdate(task);

                                    if (Thread.currentThread().isInterrupted()) {
                                        doneQueue.put(Boolean.FALSE);
                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                taskLogger.warning(resourceService.getLocalisationString("bond.classgroups.warning.threadendingexception"), e);
                                try {
                                    doneQueue.put(Boolean.FALSE);
                                } catch (InterruptedException ignored) {
                                }
                                return;

                            }
                        }
                    });
                    tasks.add(submit);
                }
                for (int i = 0; i < threads; i++) {
                    completed &= doneQueue.take();
                }
            } catch (Exception e) {
                completed = false;
//                throw e;
            } finally {
                if (execService != null) {
                    execService.shutdownNow();
                }
            }
            if(!completed) {
                taskLogger.warning(resourceService.getLocalisationString("bond.classgroups.warning.threadsnotcompleted"));
                for (Future<?> task : tasks) {
                    task.cancel(false);
                }
            }

            try {
                taskService.endTask(task);
            } catch (InvalidTaskStateException e) {
                taskLogger.error("bond.classgroups.error.invalidstate", e);
                throw e;
            }

            if (cleanupRunnable != null) {
                taskLogger.info("bond.classgroups.info.cleaningup");
                cleanupRunnable.run();
            }

            taskLogger.info("bond.classgroups.info.complete");
        } catch (Exception e) {
            if (taskLogger != null) {
                taskLogger.error("bond.classgroups.error.processfailed", e);
            }
            try {
                taskService.failTask(task);
            } catch (InvalidTaskStateException e1) {
                e1.printStackTrace();
            }
        } finally {
            cacheCleaningService.clearCaches();
        }
    }

    void handleGroup(Group group, TaskLogger taskLogger) {
        GroupManager.Status status = groupManager.syncGroup(group, taskLogger);

        if (status != GroupManager.Status.ERROR && status != GroupManager.Status.NOSYNC) {
            smartViewManager.syncSmartView(group, taskLogger);
        }
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public void setGroupManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    public SmartViewManager getSmartViewManager() {
        return smartViewManager;
    }

    public void setSmartViewManager(SmartViewManager smartViewManager) {
        this.smartViewManager = smartViewManager;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public CacheCleaningService getCacheCleaningService() {
        return cacheCleaningService;
    }

    public void setCacheCleaningService(CacheCleaningService cacheCleaningService) {
        this.cacheCleaningService = cacheCleaningService;
    }


    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskLogger getTaskLogger() {
        return taskLogger;
    }

    public void setTaskLogger(TaskLogger taskLogger) {
        this.taskLogger = taskLogger;
    }

    public Runnable getCleanupRunnable() {
        return cleanupRunnable;
    }

    public void setCleanupRunnable(Runnable cleanupRunnable) {
        this.cleanupRunnable = cleanupRunnable;
    }

    public FeedDeserialiser getFeedDeserialiser() {
        return feedDeserialiser;
    }

    public void setFeedDeserialiser(FeedDeserialiser feedDeserialiser) {
        this.feedDeserialiser = feedDeserialiser;
    }
}
