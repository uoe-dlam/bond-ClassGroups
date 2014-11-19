package au.edu.bond.classgroups.dao;

import blackboard.data.ValidationException;
import blackboard.data.course.Course;
import blackboard.data.course.Group;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.GroupDbLoader;
import blackboard.persist.course.GroupDbPersister;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by Shane Argo on 10/06/2014.
 */
public class BbGroupDAO {

    GroupDbLoader groupDbLoader;
    GroupDbPersister groupDbPersister;

    public Group getById(Id id) throws PersistenceException {
        return getGroupDbLoader().loadById(id);
    }

    public Group getById(long id) throws PersistenceException {
        return getById(getIdFromLong(id));
    }

    public Collection<Group> getByCourseId(Id id) throws PersistenceException {
        return getGroupDbLoader().loadGroupsAndSetsByCourseId(id);
    }

    public Collection<Group> getByCourseId(long id) throws PersistenceException {
        return getByCourseId(getIdFromLong(id));
    }

    public void createOrUpdate(Group group) throws PersistenceException, ValidationException {
        getGroupDbPersister().persist(group);
    }

    public void delete(Id id) throws PersistenceException {
        getGroupDbPersister().deleteById(id);
    }

    public void delete(long id) throws PersistenceException {
        delete(getIdFromLong(id));
    }

    public Id getIdFromLong(long id) {
        return Id.toId(Group.DATA_TYPE, id);
    }

    public GroupDbLoader getGroupDbLoader() throws PersistenceException {
        if(groupDbLoader == null) {
            groupDbLoader = GroupDbLoader.Default.getInstance();
        }
        return groupDbLoader;
    }

    public GroupDbPersister getGroupDbPersister() throws PersistenceException {
        if(groupDbPersister == null) {
            groupDbPersister = GroupDbPersister.Default.getInstance();
        }
        return groupDbPersister;
    }

}
