package au.edu.bond.classgroups.dao;

import blackboard.data.ValidationException;
import blackboard.data.course.Group;
import blackboard.data.course.GroupMembership;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.GroupDbLoader;
import blackboard.persist.course.GroupDbPersister;
import blackboard.persist.course.GroupMembershipDbPersister;
import blackboard.platform.course.CourseGroupManager;
import blackboard.platform.course.CourseGroupManagerFactory;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Shane Argo on 10/06/2014.
 */
public class BbGroupDAO {

    GroupDbLoader groupDbLoader;
    GroupDbPersister groupDbPersister;
    GroupMembershipDbPersister groupMembershipDbPersister;
    CourseGroupManager courseGroupManager;


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

    public void createOrUpdate(Group group, Set<Id> courseMembershipIds) throws PersistenceException, ValidationException {
        getGroupDbPersister().persist(group);

        // Persist enrolments
        Id groupId = group.getId();

        for (Id courseMembershipId : courseMembershipIds) {
            GroupMembership groupMembership = new GroupMembership();
            groupMembership.setGroupId(groupId);
            groupMembership.setCourseMembershipId(courseMembershipId);
            getGroupMembershipDbPersister().persist(groupMembership);
        }
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

    public CourseGroupManager getCourseGroupManager() {
        if (courseGroupManager == null) {
            courseGroupManager = CourseGroupManagerFactory.getInstance();
        }

        return courseGroupManager;
    }

    public GroupDbPersister getGroupDbPersister() throws PersistenceException {
        if(groupDbPersister == null) {
            groupDbPersister = GroupDbPersister.Default.getInstance();
        }

        return groupDbPersister;
    }

    public GroupMembershipDbPersister getGroupMembershipDbPersister() throws PersistenceException{
        if(groupMembershipDbPersister == null) {
            groupMembershipDbPersister = GroupMembershipDbPersister.Default.getInstance();
        }

        return groupMembershipDbPersister;
    }
}
