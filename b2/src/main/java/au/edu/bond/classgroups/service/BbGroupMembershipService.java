package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbGroupMembershipDAO;
import blackboard.data.ValidationException;
import blackboard.data.course.GroupMembership;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbGroupMembershipService {

    @Autowired
    private BbGroupMembershipDAO bbGroupMembershipDAO;

    Map<Id, GroupMembership> idCache = new HashMap<Id, GroupMembership>();
    Map</*Group Id*/Id, Map</*Group Membership Id*/Id,GroupMembership>> groupCache = new HashMap<Id, Map<Id, GroupMembership>>();

    public Collection<GroupMembership> getByGroupId(Id groupId) throws KeyNotFoundException, PersistenceException{
        Collection<GroupMembership> groupMemberships = null;
        Map<Id, GroupMembership> groupMembershipsMap = groupCache.get(groupId);
        if(groupMembershipsMap != null) {
            groupMemberships = groupMembershipsMap.values();
        } else {
            groupMemberships = bbGroupMembershipDAO.getByGroupId(groupId);
            for(GroupMembership groupMembership : groupMemberships) {
                cache(groupMembership);
            }
        }
        return groupMemberships;
    }

    public Collection<GroupMembership> getByGroupId(Id groupId, Id courseId) throws KeyNotFoundException, PersistenceException{
        Collection<GroupMembership> groupMemberships = null;
        Map<Id, GroupMembership> groupMembershipsMap = groupCache.get(groupId);
        if(groupMembershipsMap != null) {
            groupMemberships = groupMembershipsMap.values();
        } else {
            groupMemberships = new HashSet<GroupMembership>();
            Collection<GroupMembership> courseGroupMemberships = bbGroupMembershipDAO.getByCourseId(courseId);
            for(GroupMembership courseGroupMembership : courseGroupMemberships) {
                cache(courseGroupMembership);
                if(courseGroupMembership.getGroupId().equals(groupId)) {
                    groupMemberships.add(courseGroupMembership);
                }
            }
        }
        return groupMemberships;
    }

    public void createOrUpdate(GroupMembership groupMembership) throws ValidationException, PersistenceException {
        bbGroupMembershipDAO.createOrUpdate(groupMembership);
        cache(groupMembership);
    }

    public void delete(long id) throws PersistenceException {
        delete(getIdFromLong(id));
    }

    public void delete(Id id) throws PersistenceException {
        bbGroupMembershipDAO.delete(id);
        uncache(id);
    }

    public Id getIdFromLong(long id) {
        return Id.toId(GroupMembership.DATA_TYPE, id);
    }

    private void cache(GroupMembership groupMembership) {
        Id groupId = groupMembership.getGroupId();
        Map<Id, GroupMembership> groupMembershipsMap = groupCache.get(groupMembership.getGroupId());
        if(groupMembershipsMap == null) {
            groupMembershipsMap = new HashMap<Id, GroupMembership>();
            groupCache.put(groupId, groupMembershipsMap);
        }
        groupMembershipsMap.put(groupMembership.getId(), groupMembership);
        idCache.put(groupMembership.getId(), groupMembership);
    }

    private void uncache(Id id) {
        GroupMembership groupMembership = idCache.get(id);
        if(groupMembership != null) {
            idCache.remove(id);
            Map<Id, GroupMembership> groupMembershipsMap = groupCache.get(groupMembership.getGroupId());
            if (groupMembershipsMap != null) {
                groupCache.remove(groupMembership.getId());
            }
        }
    }

    public BbGroupMembershipDAO getBbGroupMembershipDAO() {
        return bbGroupMembershipDAO;
    }

    public void setBbGroupMembershipDAO(BbGroupMembershipDAO bbGroupMembershipDAO) {
        this.bbGroupMembershipDAO = bbGroupMembershipDAO;
    }

}
