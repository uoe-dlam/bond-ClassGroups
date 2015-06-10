package au.edu.bond.classgroups.groupext;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.io.Closeable;
import java.util.Collection;
import java.util.List;

/**
 * Created by Shane Argo on 5/06/2014.
 */
public class GroupExtensionDAO implements Closeable {

    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public GroupExtensionDAO(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public GroupExtension getByExternalSystemId(String externalSystemId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<GroupExtension> query = entityManager.createQuery("FROM GroupExtension WHERE externalSystemId = :externalSystemId", GroupExtension.class);
        query.setParameter("externalSystemId", externalSystemId);
        final GroupExtension groupExtension = query.getSingleResult();
        entityManager.close();
        return groupExtension;
    }

    public Collection<GroupExtension> getAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<GroupExtension> query = entityManager.createQuery("FROM GroupExtension", GroupExtension.class);
        final List<GroupExtension> groupExtensions = query.getResultList();
        entityManager.close();
        return groupExtensions;
    }

    public GroupExtension get(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        final GroupExtension groupExtension = entityManager.find(GroupExtension.class, id);
        entityManager.close();
        return groupExtension;
    }

    public Collection<GroupExtension> getByGroupIds(Collection<Long> groupIds) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TypedQuery<GroupExtension> query = entityManager.createQuery("FROM GroupExtension WHERE internalGroupId IN :ids", GroupExtension.class);
        query.setParameter("ids", groupIds);
        final List<GroupExtension> groupExtensions = query.getResultList();
        entityManager.close();
        return groupExtensions;
    }

    public void create(GroupExtension ext) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            entityManager.persist(ext);
            entityTransaction.commit();
        } catch(RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public void update(GroupExtension ext) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            entityManager.merge(ext);
            entityTransaction.commit();
        } catch(RuntimeException e) {
            entityTransaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public void delete(GroupExtension ext) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();
        try {
            entityManager.remove(ext);
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
