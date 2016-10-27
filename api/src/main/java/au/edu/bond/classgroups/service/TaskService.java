package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.TaskDAO;
import au.edu.bond.classgroups.exception.InvalidTaskStateException;
import au.edu.bond.classgroups.model.Schedule;
import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.util.ScheduleUtil;
import au.edu.bond.classgroups.util.ServerUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by Shane Argo on 12/06/2014.
 */
public class TaskService implements Closeable {

    public static final long THROTTLE_MIN_INTERVAL_MILLISECONDS = 1000;

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private DirectoryFactory directoryFactory;

    private final Date lastStatUpdate = new Date(0);

    public Task getById(Long id) {
        return getById(id, null);
    }

    private Task getById(Long id, EntityManager entityManager) {
        if(entityManager == null) {
            return taskDAO.get(id);
        } else {
            return taskDAO.get(id, entityManager);
        }
    }


    public Task getMostRecentlyStarted() {
        return getMostRecentlyStarted(null);
    }

    private Task getMostRecentlyStarted(EntityManager entityManager) {
        if(entityManager == null) {
            return taskDAO.getMostRecentlyStarted();
        } else {
            return taskDAO.getMostRecentlyStarted(entityManager);
        }
    }

    public Task getCurrentTask() {
        return getCurrentTask(null);
    }

    private Task getCurrentTask(EntityManager entityManager) {
        final List<Task> tasks;
        if(entityManager == null) {
            tasks = taskDAO.getByStatus(Task.Status.PROCESSING);
        } else {
            tasks = taskDAO.getByStatus(Task.Status.PROCESSING, entityManager);
        }
        if(tasks.size() == 0) {
            return null;
        }
        return tasks.get(0);
    }

    public Task createTask() {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();

            Task task = createNewTask();

            taskDAO.create(task, entityManager);
            entityTransaction.commit();

            return task;
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                try {
                    entityTransaction.rollback();
                } catch(RuntimeException ignored) {
                }
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    public Task prepareScheduledTask(Task task) throws InvalidTaskStateException {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();

            final Task result = prepareScheduledTask(task, entityManager);
            entityTransaction.commit();
            return result;
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                try {
                    entityTransaction.rollback();
                } catch(RuntimeException ignored) {
                }
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    public Task prepareScheduledTask(Task task, EntityManager entityManager) throws InvalidTaskStateException {
        if(task.getStatus() != Task.Status.SCHEDULED) {
            throw new InvalidTaskStateException(
                    resourceService.getLocalisationString(
                            "bond.classgroups.exception.cannotpreparesched",
                            task.getStatus().name()));
        }

        final Task existingTask = getCurrentTask(entityManager);
        if(existingTask != null) {
            if(existingTask.getId().equals(task.getId())) {
                return null;
            }

            Date now = new Date();
            task.setStartedDate(now);
            task.setEndedDate(now);
            task.setStatus(Task.Status.SKIPPED);
            taskDAO.update(task, entityManager);
        } else {
            task = taskDAO.beginScheduled(task.getId(), ServerUtil.getServerName(), entityManager);

            if(task == null) {
                return null;
            }

        }
        return task;
    }

    public void pendingTask(Task task) throws InvalidTaskStateException {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();

            pendingTask(task, entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                try {
                    entityTransaction.rollback();
                } catch(RuntimeException ignored) {
                }
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void pendingTask(Task task, EntityManager entityManager) throws InvalidTaskStateException {
        Task transactionTask = taskDAO.get(task.getId(), entityManager);

        if(transactionTask.getStatus() != task.getStatus()) {
            throw new InvalidTaskStateException(resourceService.getLocalisationString(
                    "bond.classgroups.exception.taskupdatedexternally",
                    task.getStatus(), transactionTask.getStatus()));
        }

        if(task.getStatus() != Task.Status.NEW) {
            throw new InvalidTaskStateException(
                    resourceService.getLocalisationString(
                            "bond.classgroups.exception.cannotbeginnotnew",
                            task.getStatus().name()));
        }

        task.setStatus(Task.Status.PENDING);
        taskDAO.update(task, entityManager);
    }

    public void beginTask(Task task) throws InvalidTaskStateException {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();
            beginTask(task, entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                try {
                    entityTransaction.rollback();
                } catch(RuntimeException ignored) {
                }
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void beginTask(Task task, EntityManager entityManager) throws InvalidTaskStateException {
        Task transactionTask = taskDAO.get(task.getId(), entityManager);

        if(transactionTask.getStatus() != task.getStatus()) {
            throw new InvalidTaskStateException(resourceService.getLocalisationString(
                    "bond.classgroups.exception.taskupdatedexternally",
                    task.getStatus(), transactionTask.getStatus()));
        }

        if(task.getStatus() != Task.Status.PENDING) {
            throw new InvalidTaskStateException(
                    resourceService.getLocalisationString(
                            "bond.classgroups.exception.cannotbeginnotpending",
                            task.getStatus().name()));
        }

        task.setStatus(Task.Status.PROCESSING);
        task.setProcessingNode(ServerUtil.getServerName());
        Date now = new Date();
        task.setStartedDate(now);
        taskDAO.update(task, entityManager);
    }

    public Task getNextPending() {
        return getNextPending(null);
    }

    private Task getNextPending(EntityManager entityManager) {
        try {
            if (entityManager == null) {
                return taskDAO.getNextPending();
            } else {
                return taskDAO.getNextPending(entityManager);
            }
        } catch (NoResultException e) {
            return null;
        }
    }

    public void endTask(Task task) throws InvalidTaskStateException {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();

            endTask(task, entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                try {
                    entityTransaction.rollback();
                } catch(RuntimeException ignored) {
                }
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }


    }

    public void endTask(Task task, EntityManager entityManager) throws InvalidTaskStateException {
            Task transactionTask = taskDAO.get(task.getId(), entityManager);

            if (transactionTask.getStatus() != task.getStatus()) {
                throw new InvalidTaskStateException(resourceService.getLocalisationString(
                        "bond.classgroups.exception.taskupdatedexternally",
                        task.getStatus(), transactionTask.getStatus()));
            }

            if (task.getStatus() != Task.Status.PROCESSING) {
                throw new InvalidTaskStateException(
                        resourceService.getLocalisationString(
                                "bond.classgroups.exception.cannotendnotprocessing",
                                task.getStatus().name()));
            }

            task.setStatus(Task.Status.COMPLETE);
            Date now = new Date();
            task.setEndedDate(now);
            taskDAO.update(task, entityManager);
    }

    public void failTask(Task task) throws InvalidTaskStateException {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();

            failTask(task, entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                try {
                    entityTransaction.rollback();
                } catch(RuntimeException ignored) {
                }
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void failTask(Task task, EntityManager entityManager) throws InvalidTaskStateException {
            Task transactionTask = taskDAO.get(task.getId(), entityManager);

            if (transactionTask.getStatus() != task.getStatus()) {
                throw new InvalidTaskStateException(
                        resourceService.getLocalisationString(
                                "bond.classgroups.exception.taskupdatedexternally",
                                task.getStatus(), transactionTask.getStatus()));
            }

            task.setStatus(Task.Status.FAILED);
            Date now = new Date();
            task.setEndedDate(now);
            taskDAO.update(task);
    }

    public void cancelTask(Task task) throws InvalidTaskStateException {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;

        entityTransaction.begin();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();
            cancelTask(task, entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                try {
                    entityTransaction.rollback();
                } catch(RuntimeException ignored) {
                }
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }

        }
    }

    public void cancelTask(Task task, EntityManager entityManager) throws InvalidTaskStateException {
        Task transactionTask = taskDAO.get(task.getId(), entityManager);

        if (transactionTask.getStatus() != task.getStatus()) {
            throw new InvalidTaskStateException(resourceService.getLocalisationString(
                    "bond.classgroups.exception.taskupdatedexternally",
                    task.getStatus(), transactionTask.getStatus()));
        }

        if (task.getStatus() != Task.Status.SCHEDULED) {
            throw new InvalidTaskStateException(
                    resourceService.getLocalisationString(
                            "bond.classgroups.exception.cannotcancelnotsched",
                            task.getStatus().name()));
        }

        task.setStatus(Task.Status.CANCELLED);
        Date now = new Date();
        task.setEndedDate(now);
        taskDAO.update(task, entityManager);
    }

    public void throttledUpdate(Task task) {
        Date now = new Date();
        if((now.getTime() - lastStatUpdate.getTime()) > THROTTLE_MIN_INTERVAL_MILLISECONDS) {
            synchronized (lastStatUpdate) {
                if((now.getTime() - lastStatUpdate.getTime()) > THROTTLE_MIN_INTERVAL_MILLISECONDS) {
                    taskDAO.update(task);
                    lastStatUpdate.setTime(now.getTime());
                }
            }
        }
    }

    public Collection<Task> getAll() {
        return taskDAO.getAll();
    }

    public void deleteOlderThan(Date date) {
        final Collection<Long> deletedIds = taskDAO.deleteOlderThan(date);
        for (Long deletedId : deletedIds) {
            final File feedDirectory = directoryFactory.getFeedDirectory(deletedId);
            FileUtils.deleteQuietly(feedDirectory);
        }
    }

    public void deleteOlderThan(int days) {
        if(days <= 0) {
            return;
        }

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1 * days);

        deleteOlderThan(calendar.getTime());
    }

    private Task createBaseTask() {
        Task task = new Task();
        Date now = new Date();
        task.setEnteredDate(now);

        task.setEnteredNode(ServerUtil.getServerName());

        return task;
    }

    private Task createNewTask() {
        Task task = createBaseTask();
        task.setStatus(Task.Status.NEW);
        return task;
    }

    private Task createPendingTask() {
        Task task = createBaseTask();
        task.setStatus(Task.Status.PENDING);
        return task;
    }

    private Task createSkippedTask() {
        Task task = createBaseTask();
        Date now = new Date();
        task.setStartedDate(now);
        task.setEndedDate(now);
        task.setStatus(Task.Status.SKIPPED);
        return task;
    }

    private Task createScheduledTask(Date scheduledDate) {
        Task task = createBaseTask();
        task.setStatus(Task.Status.SCHEDULED);
        task.setScheduledDate(scheduledDate);
        return task;
    }

    public Task updateSchedule(boolean enabled, Collection<Schedule> schedules) {
        Calendar nextOccurrenceCal = schedules == null ? null : ScheduleUtil.GetNextOccurrence(schedules);
        Date nextOccurrence = nextOccurrenceCal == null ? null : nextOccurrenceCal.getTime();

        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            Task task = null;

            entityTransaction.begin();

            final List<Task> scheduledTasks = taskDAO.getByStatus(Task.Status.SCHEDULED, entityManager);

            for(Task scheduledTask : scheduledTasks) {
                if(nextOccurrence != null && scheduledTask.getScheduledDate().getTime() == nextOccurrence.getTime() && enabled) {
                    task = scheduledTask;
                } else {
                    try {
                        cancelTask(scheduledTask, entityManager);
                    } catch (InvalidTaskStateException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(enabled && task == null && nextOccurrence != null) {
                task = createScheduledTask(nextOccurrence);
                task = taskDAO.createScheduled(task, entityManager);
            }

            entityTransaction.commit();

            return task;
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                try {
                    entityTransaction.rollback();
                } catch(RuntimeException ignored) {
                }
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public DirectoryFactory getDirectoryFactory() {
        return directoryFactory;
    }

    public void setDirectoryFactory(DirectoryFactory directoryFactory) {
        this.directoryFactory = directoryFactory;
    }

    @Override
    public void close() throws IOException {
        if(entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
