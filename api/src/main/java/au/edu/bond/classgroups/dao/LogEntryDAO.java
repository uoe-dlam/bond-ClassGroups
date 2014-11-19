package au.edu.bond.classgroups.dao;

import au.edu.bond.classgroups.model.LogEntry;
import au.edu.bond.classgroups.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.io.Closeable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by Shane Argo on 11/06/2014.
 */
public class LogEntryDAO implements Closeable {

    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public LogEntryDAO(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Collection<LogEntry> getAllForTask(Task task) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<LogEntry> query = entityManager.createQuery("FROM LogEntry WHERE taskId = :taskId", LogEntry.class);
        query.setParameter("taskId", task.getId());
        final List<LogEntry> logEntries = query.getResultList();
        entityManager.close();
        return logEntries;
    }

    public Collection<LogEntry> getAllForTaskAfterId(Task task, Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<LogEntry> query = entityManager.createQuery("FROM LogEntry " +
                "WHERE taskId = :taskId " +
                "AND id > :afterId", LogEntry.class);
        query.setParameter("taskId", task.getId());
        query.setParameter("afterId", id);
        final List<LogEntry> logEntries = query.getResultList();
        entityManager.close();
        return logEntries;
    }

    public LogEntry get(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        final LogEntry logEntry = entityManager.find(LogEntry.class, id);
        entityManager.close();
        return logEntry;
    }

    public void create(LogEntry entry) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            entityManager.persist(entry);
            entityTransaction.commit();
        } catch(RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public void update(LogEntry entry) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            entityManager.remove(entry);
            entityTransaction.commit();
        } catch(RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public void delete(LogEntry entry) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            entityManager.remove(entry);
            entityTransaction.commit();
        } catch(RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }


    public void delete(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            entityManager.remove(get(id));
            entityTransaction.commit();
        } catch(RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void close() {
        if(entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
