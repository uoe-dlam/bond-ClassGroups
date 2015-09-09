package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.TaskDAO;
import au.edu.bond.classgroups.exception.InvalidTaskStateException;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.model.Schedule;
import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.util.ScheduleUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.Closeable;
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
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        Task task;

        entityTransaction.begin();
        try {
            task = createPendingTask();

            taskDAO.create(task, entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }

        return task;
    }

    public Task prepareScheduledTask(Task task) throws InvalidTaskStateException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            final Task result = prepareScheduledTask(task, entityManager);
            entityTransaction.commit();
            return result;
        } catch (RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
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
            task = taskDAO.beginScheduled(task.getId(), getServerName(), entityManager);

            if(task == null) {
                return null;
            }

        }
        return task;
    }

    public void beginTask(Task task) throws InvalidTaskStateException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            beginTask(task, entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
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
        task.setProcessingNode(getServerName());
        Date now = new Date();
        task.setStartedDate(now);
        taskDAO.update(task, entityManager);
    }

    public Task beginNextTask(EntityManager entityManager) {

    }

    public void endTask(Task task) throws InvalidTaskStateException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            endTask(task, entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
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
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            failTask(task, entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();

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
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            cancelTask(task, entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();

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
        taskDAO.deleteOlderThan(date);
    }

    public void deleteOlderThan(int days) {
        if(days <= 0) {
            return;
        }

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1*days);

        deleteOlderThan(calendar.getTime());
    }

    private Task createBaseTask() {
        Task task = new Task();
        Date now = new Date();
        task.setEnteredDate(now);

        task.setEnteredNode(getServerName());

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

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        Task task = null;

        entityTransaction.begin();
        try {
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
        } catch (RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }

        return task;
    }

    private String getServerName() {
        String server = null;
        try {
            server = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return server;
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

    @Override
    public void close() throws IOException {
        if(entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
