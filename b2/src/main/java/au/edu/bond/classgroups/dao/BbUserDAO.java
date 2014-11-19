package au.edu.bond.classgroups.dao;

import blackboard.data.ValidationException;
import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.persist.user.UserDbLoader;
import blackboard.persist.user.UserDbPersister;

import java.util.Collection;

/**
 * Created by Shane Argo on 10/06/2014.
 */
public class BbUserDAO {

    UserDbLoader userDbLoader;
//    UserDbPersister userDbPersister;


    public User getById(Id id) throws PersistenceException {
        return getUserDbLoader().loadById(id);
    }

    public User getById(long id) throws PersistenceException {
        Id bbId = Id.toId(User.DATA_TYPE, id);
        return getById(bbId);
    }

    public Collection<User> getByCourseId(Id id) throws PersistenceException {
        return getUserDbLoader().loadByCourseId(id);
    }

    public Collection<User> getByCourseId(long id) throws PersistenceException {
        Id bbId = Id.toId(Course.DATA_TYPE, id);
        return getByCourseId(id);
    }

    public User getByExternalSystemId(String externalSystemId) throws PersistenceException {
        return getUserDbLoader().loadByBatchUid(externalSystemId);
    }

//    public void createOrUpdate(User user) throws PersistenceException, ValidationException {
//        getUserDbPersister().persist(user);
//    }
//
//    public void delete(long id) throws PersistenceException {
//        Id bbId = Id.toId(User.DATA_TYPE, id);
//        getUserDbPersister().deleteById(bbId);
//    }

    public UserDbLoader getUserDbLoader() throws PersistenceException {
        if(userDbLoader == null) {
            userDbLoader = UserDbLoader.Default.getInstance();
        }
        return userDbLoader;
    }

//    public UserDbPersister getUserDbPersister() throws PersistenceException {
//        if(userDbPersister == null) {
//            userDbPersister = UserDbPersister.Default.getInstance();
//        }
//        return userDbPersister;
//    }

}
