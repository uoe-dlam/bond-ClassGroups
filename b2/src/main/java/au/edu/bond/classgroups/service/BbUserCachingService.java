package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbUserDAO;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbUserCachingService implements BbUserService {

    @Autowired
    private BbUserDAO bbUserDAO;

    Map<Id, User> idCache = new HashMap<Id, User>();
    Map<String, User> externalSystemIdCache = new HashMap<String, User>();

    @Override
    public User getById(Id id) throws PersistenceException {
        User user = idCache.get(id);
        if(user == null) {
            user = bbUserDAO.getById(id);
            cache(user);
        }
        return user;
    }

    @Override
    public User getByExternalSystemId(String externalSystemId) throws PersistenceException {
        User user = externalSystemIdCache.get(externalSystemId);
        if(user == null) {
            user = bbUserDAO.getByExternalSystemId(externalSystemId);
            cache(user);
        }
        return user;
    }

    @Override
    public User getByExternalSystemId(String externalSystemId, Id courseId) throws PersistenceException {
        User user = externalSystemIdCache.get(externalSystemId);
        if(user == null) {
            Collection<User> courseUsers = bbUserDAO.getByCourseId(courseId);
            for(User courseUser : courseUsers) {
                cache(courseUser);
                if(courseUser.getBatchUid().equals(externalSystemId)){
                    user = courseUser;
                }
            }
        }
        return user;
    }

    private void cache(User user) {
        idCache.put(user.getId(), user);
        externalSystemIdCache.put(user.getBatchUid(), user);
    }

    public BbUserDAO getBbUserDAO() {
        return bbUserDAO;
    }

    public void setBbUserDAO(BbUserDAO bbUserDAO) {
        this.bbUserDAO = bbUserDAO;
    }
}
