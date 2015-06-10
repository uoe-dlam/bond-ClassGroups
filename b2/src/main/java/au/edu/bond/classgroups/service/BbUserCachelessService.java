package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbUserDAO;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Created by shane on 20/01/15.
 */
public class BbUserCachelessService implements BbUserService {

    @Autowired
    private BbUserDAO bbUserDAO;

    @Override
    public User getById(Id id) throws PersistenceException {
        return bbUserDAO.getById(id);
    }

    @Override
    public User getByExternalSystemId(String externalSystemId) throws PersistenceException {
        return bbUserDAO.getByExternalSystemId(externalSystemId);
    }

    @Override
    public User getByExternalSystemId(String externalSystemId, Id courseId) throws PersistenceException {
        Collection<User> courseUsers = bbUserDAO.getByCourseId(courseId);
        for(User courseUser : courseUsers) {
            if(courseUser.getBatchUid().equals(externalSystemId)){
                return courseUser;
            }
        }
        return null;
    }

    public BbUserDAO getBbUserDAO() {
        return bbUserDAO;
    }

    public void setBbUserDAO(BbUserDAO bbUserDAO) {
        this.bbUserDAO = bbUserDAO;
    }
}
