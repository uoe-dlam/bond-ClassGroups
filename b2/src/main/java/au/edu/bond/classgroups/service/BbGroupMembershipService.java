package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbGroupMembershipDAO;
import blackboard.data.ValidationException;
import blackboard.data.course.GroupMembership;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbGroupMembershipService {

    @Autowired
    private BbGroupMembershipDAO bbGroupMembershipDAO;

    private LoadingCache</*Course Id*/Id, ConcurrentMap</*Group Id*/Id, ConcurrentMap</*Group Membership Id*/Id, GroupMembership>>> byIdCache;

    public BbGroupMembershipService(String byIdCacheSpec) {
        byIdCache = CacheBuilder.from(byIdCacheSpec).build(new CacheLoader<Id, ConcurrentMap<Id, ConcurrentMap<Id, GroupMembership>>>() {
            @Override
            public ConcurrentMap<Id, ConcurrentMap<Id, GroupMembership>> load(Id courseId) throws Exception {
                final ConcurrentMap<Id, ConcurrentMap<Id, GroupMembership>> groupMap = new ConcurrentHashMap<Id, ConcurrentMap<Id, GroupMembership>>();
                final Collection<GroupMembership> members = bbGroupMembershipDAO.getByCourseId(courseId);
                for (GroupMembership member : members) {
                    final Id groupId = member.getGroupId();
                    ConcurrentMap<Id, GroupMembership> memberMap = groupMap.get(groupId);
                    if(memberMap == null) {
                        memberMap = new ConcurrentHashMap<Id, GroupMembership>();
                        groupMap.put(groupId, memberMap);
                    }
                    memberMap.put(member.getId(), member);
                }
                return groupMap;
            }
        });
    }

    public Collection<GroupMembership> getByGroupId(Id groupId, Id courseId) throws ExecutionException {
        return byIdCache.get(courseId).get(groupId).values();
    }

    public void createOrUpdate(GroupMembership groupMembership, Id courseId) throws ValidationException, PersistenceException, ExecutionException {
        bbGroupMembershipDAO.createOrUpdate(groupMembership);

        final Id groupId = groupMembership.getGroupId();
        final ConcurrentMap<Id, ConcurrentMap<Id, GroupMembership>> groupMap = byIdCache.get(courseId);
        ConcurrentMap<Id, GroupMembership> memberMap = groupMap.get(groupId);
        if(memberMap == null) {
            memberMap = new ConcurrentHashMap<Id, GroupMembership>();
            groupMap.put(groupId, memberMap);
        }
        memberMap.put(groupMembership.getId(), groupMembership);
    }

    public void delete(Id groupMembershipId, Id groupId, Id courseId) throws PersistenceException, ExecutionException {
        bbGroupMembershipDAO.delete(groupMembershipId);

        ConcurrentMap<Id, GroupMembership> memberMap = byIdCache.get(courseId).get(groupId);
        if(memberMap != null) {
            memberMap.remove(groupMembershipId);
        }
    }

    public Id getIdFromLong(long id) {
        return Id.toId(GroupMembership.DATA_TYPE, id);
    }

    public BbGroupMembershipDAO getBbGroupMembershipDAO() {
        return bbGroupMembershipDAO;
    }

    public void setBbGroupMembershipDAO(BbGroupMembershipDAO bbGroupMembershipDAO) {
        this.bbGroupMembershipDAO = bbGroupMembershipDAO;
    }

}
