package au.edu.bond.classgroups.task;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.exception.FeedDeserialisationException;
import au.edu.bond.classgroups.exception.InvalidTaskStateException;
import au.edu.bond.classgroups.feed.FeedDeserialiser;
import au.edu.bond.classgroups.feed.FeedDeserialiserFactory;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.manager.GroupManager;
import au.edu.bond.classgroups.manager.SmartViewManager;
import au.edu.bond.classgroups.model.Group;
import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.CacheCleaningService;
import au.edu.bond.classgroups.service.ResourceService;
import au.edu.bond.classgroups.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Shane Argo on 10/06/2014.
 */
public class TaskProcessor implements Runnable {

    public static final int MINIMUM_GROUPS_FOR_MULTITHREADING = 20;

    @Autowired
    private TaskService taskService;
    @Autowired
    private FeedDeserialiserFactory feedDeserialiserFactory;
    @Autowired
    private Task currentTask;
    @Autowired
    private TaskLogger currentTaskLogger;
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

    private FeedDeserialiser feedDeserialiser;
    private Runnable cleanupRunnable;

    @Override
    public void run() {
        try {
            currentTaskLogger.info("bond.classgroups.info.begin");
            try {
                taskService.beginTask(currentTask);
            } catch (InvalidTaskStateException e) {
                currentTaskLogger.error("bond.classgroups.error.invalidstate", e);
                throw e;
            }

            if (feedDeserialiser == null) {
                currentTaskLogger.info("bond.classgroups.info.preconfigdeserialiser");
                feedDeserialiser = feedDeserialiserFactory.getDefault();
            }

            if (feedDeserialiser == null) {
                throw new Exception(resourceService.getLocalisationString("bond.classgroups.error.nopreconfigdeserialiser"));

            }

            currentTaskLogger.info("bond.classgroups.info.consumingfeed");
            List<Group> groups = null;
            try {
                groups = new ArrayList<Group>(feedDeserialiser.getGroups());
            } catch (FeedDeserialisationException e) {
                currentTaskLogger.error("bond.classgroups.error.faileddeserialise", e);
                throw e;
            }

            Collections.sort(groups, new Comparator<Group>() {
                @Override
                public int compare(Group left, Group right) {
                    return left.getCourseId().compareTo(right.getCourseId());
                }
            });

            final int total = groups.size();
            currentTaskLogger.info(resourceService.getLocalisationString("bond.classgroups.info.feedconsumed", total));
            currentTask.setTotalGroups(total);
            currentTask.setProcessedGroups(0);
            taskService.throttledUpdate(currentTask);

            final ConcurrentLinkedQueue<Group> groupsQueue = new ConcurrentLinkedQueue<Group>(groups);
            currentTaskLogger.info("bond.classgroups.info.updatinggroups");

            int threads = total >= MINIMUM_GROUPS_FOR_MULTITHREADING && configuration.getProcessingThreads() > 0 ? configuration.getProcessingThreads() : 1;
            ExecutorService execService = null;
            try {
                execService = Executors.newFixedThreadPool(threads);
                final ArrayBlockingQueue<Boolean> doneQueue = new ArrayBlockingQueue<>(threads);
                for (int i = 0; i < threads; i++) {
                    execService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    final Group group = groupsQueue.poll();
                                    if (group == null) {
                                        doneQueue.put(Boolean.TRUE);
                                        return;
                                    }
                                    handleGroup(group);

                                    currentTask.setProcessedGroups(total - groupsQueue.size());
                                    taskService.throttledUpdate(currentTask);

                                    if (Thread.currentThread().isInterrupted()) {
//                                        throw new Exception(resourceService.getLocalisationString("bond.classgroups.error.threadinterrupted"));
                                        doneQueue.put(Boolean.FALSE);
                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                try {
                                    currentTaskLogger.error("Oh shii-", e);
                                    doneQueue.put(Boolean.FALSE);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        }
                    });

                }
                for (int i = 0; i < threads; i++) {
                    doneQueue.take();
                }
            } finally {
                if(execService != null) {
                    execService.shutdownNow();
                }
            }

            try {
                taskService.endTask(currentTask);
            } catch (InvalidTaskStateException e) {
                currentTaskLogger.error("bond.classgroups.error.invalidstate", e);
                throw e;
            }

            if (cleanupRunnable != null) {
                currentTaskLogger.info("bond.classgroups.info.cleaningup");
                cleanupRunnable.run();
            }

            currentTaskLogger.info("bond.classgroups.info.complete");
        } catch (Exception e) {
            if (currentTaskLogger != null) {
                currentTaskLogger.error("bond.classgroups.error.processfailed", e);
            }
            try {
                taskService.failTask(currentTask);
            } catch (InvalidTaskStateException e1) {
                e1.printStackTrace();
            }
        } finally {
            cacheCleaningService.clearCaches();
        }
    }

    void handleGroup(Group group) {
        GroupManager.Status status = groupManager.syncGroup(group);

        if (status != GroupManager.Status.ERROR && status != GroupManager.Status.NOSYNC) {
            smartViewManager.syncSmartView(group);
        }
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public FeedDeserialiserFactory getFeedDeserialiserFactory() {
        return feedDeserialiserFactory;
    }

    public void setFeedDeserialiserFactory(FeedDeserialiserFactory feedDeserialiserFactory) {
        this.feedDeserialiserFactory = feedDeserialiserFactory;
    }

    public FeedDeserialiser getFeedDeserialiser() {
        return feedDeserialiser;
    }

    public void setFeedDeserialiser(FeedDeserialiser feedDeserialiser) {
        this.feedDeserialiser = feedDeserialiser;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

    public TaskLogger getCurrentTaskLogger() {
        return currentTaskLogger;
    }

    public void setCurrentTaskLogger(TaskLogger currentTaskLogger) {
        this.currentTaskLogger = currentTaskLogger;
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

    public Runnable getCleanupRunnable() {
        return cleanupRunnable;
    }

    public void setCleanupRunnable(Runnable cleanupRunnable) {
        this.cleanupRunnable = cleanupRunnable;
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
}
