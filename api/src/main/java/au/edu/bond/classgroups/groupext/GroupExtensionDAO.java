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
public class GroupExtensionDAO implements AutoCloseable {

    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public GroupExtensionDAO(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public GroupExtension getByExternalSystemId(String externalSystemId) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            TypedQuery<GroupExtension> query = entityManager.createQuery("FROM GroupExtension WHERE externalSystemId = :externalSystemId", GroupExtension.class);
            query.setParameter("externalSystemId", externalSystemId);
            return query.getSingleResult();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public Collection<GroupExtension> getByCourseId(long courseId) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            TypedQuery<GroupExtension> query = entityManager.createQuery("FROM GroupExtension WHERE courseId = :courseId", GroupExtension.class);
            query.setParameter("courseId", courseId);
            return query.getResultList();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public Collection<GroupExtension> getAll() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            TypedQuery<GroupExtension> query = entityManager.createQuery("FROM GroupExtension", GroupExtension.class);
            return query.getResultList();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public GroupExtension get(long id) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            return entityManager.find(GroupExtension.class, id);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public Collection<GroupExtension> getByGroupIds(Collection<Long> groupIds) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            TypedQuery<GroupExtension> query = entityManager.createQuery("FROM GroupExtension WHERE internalGroupId IN :ids", GroupExtension.class);
            query.setParameter("ids", groupIds);
            return query.getResultList();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void create(GroupExtension ext) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();
            entityManager.persist(ext);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void update(GroupExtension ext) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();
            entityManager.merge(ext);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void delete(GroupExtension ext) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();
            entityManager.remove(ext);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }


    public void delete(long id) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();
            entityManager.remove(get(id));
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if(entityTransaction != null) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public void close() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
