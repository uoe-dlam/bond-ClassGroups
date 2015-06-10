package au.edu.bond.classgroups.dao;

import blackboard.data.ValidationException;
import blackboard.data.course.Group;
import blackboard.data.course.GroupMembership;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.GroupMembershipDbLoader;
import blackboard.persist.course.GroupMembershipDbPersister;

import java.util.Collection;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbGroupMembershipDAO {

    private GroupMembershipDbLoader groupMembershipDbLoader;
    private GroupMembershipDbPersister groupMembershipDbPersister;

    public GroupMembership getById(long id) throws PersistenceException {
        Id bbId = Id.toId(GroupMembership.DATA_TYPE, id);
        return getGroupMembershipDbLoader().loadById(bbId);
    }

    public Collection<GroupMembership> getByGroupId(Id id) throws PersistenceException {
        return getGroupMembershipDbLoader().loadByGroupId(id);
    }

    public Collection<GroupMembership> getByCourseId(Id id) throws PersistenceException {
        return getGroupMembershipDbLoader().loadByCourseId(id);
    }

    public GroupMembership getByGroupIdAndUserId(Id groupId, Id userId) throws PersistenceException {
        return getGroupMembershipDbLoader().loadByGroupAndUserId(groupId, userId);
    }

    public void createOrUpdate(GroupMembership groupMembership) throws PersistenceException, ValidationException {
        getGroupMembershipDbPersister().persist(groupMembership);
    }

    public void delete(long id) throws PersistenceException {
        Id bbId = Id.toId(GroupMembership.DATA_TYPE, id);
        getGroupMembershipDbPersister().deleteById(bbId);
    }

    public void delete(Id id) throws PersistenceException {
        getGroupMembershipDbPersister().deleteById(id);
    }

    public GroupMembershipDbLoader getGroupMembershipDbLoader() throws PersistenceException {
        if(groupMembershipDbLoader == null) {
            groupMembershipDbLoader = GroupMembershipDbLoader.Default.getInstance();
        }
        return groupMembershipDbLoader;
    }

    public GroupMembershipDbPersister getGroupMembershipDbPersister() throws PersistenceException {
        if(groupMembershipDbPersister == null) {
            groupMembershipDbPersister = GroupMembershipDbPersister.Default.getInstance();
        }
        return groupMembershipDbPersister;
    }

}
