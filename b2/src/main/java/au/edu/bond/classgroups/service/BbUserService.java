package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbUserDAO;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbUserService {

    @Autowired
    private BbUserDAO bbUserDAO;

    private LoadingCache</*Course Id*/Id, ConcurrentMap</*User Id*/Id, User>> byIdCache;
    private LoadingCache</*Course Id*/Id, ConcurrentMap</*External System Id*/String, User>> byEsidCache;

    public BbUserService(String byIdCacheSpec, String byEsidCacheSpec) {
        byIdCache = CacheBuilder.from(byIdCacheSpec).build(new CacheLoader<Id, ConcurrentMap<Id, User>>() {
            @Override
            public ConcurrentMap<Id, User> load(Id courseId) throws Exception {
                ConcurrentHashMap<Id, User> idMap = new ConcurrentHashMap<Id, User>();
                Collection<User> users = bbUserDAO.getByCourseId(courseId);
                for (User user : users) {
                    idMap.put(user.getId(), user);
                }
                return idMap;
            }
        });

        byEsidCache = CacheBuilder.from(byEsidCacheSpec).build(new CacheLoader<Id, ConcurrentMap<String, User>>() {
            @Override
            public ConcurrentMap<String, User> load(Id courseId) throws Exception {
                ConcurrentHashMap<String, User> esidMap = new ConcurrentHashMap<String, User>();
                Collection<User> users = byIdCache.get(courseId).values();
                for (User user : users) {
                    esidMap.put(user.getBatchUid(), user);
                }
                return esidMap;
            }
        });
    }

    public User getById(Id userId, Id courseId) throws ExecutionException {
        return byIdCache.get(courseId).get(userId);
    }

    public User getByExternalSystemId(String externalSystemId, Id courseId) throws ExecutionException {
        return byEsidCache.get(courseId).get(externalSystemId);
    }

    public BbUserDAO getBbUserDAO() {
        return bbUserDAO;
    }

    public void setBbUserDAO(BbUserDAO bbUserDAO) {
        this.bbUserDAO = bbUserDAO;
    }
}
