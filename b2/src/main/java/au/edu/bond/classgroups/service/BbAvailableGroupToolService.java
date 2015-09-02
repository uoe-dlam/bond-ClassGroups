package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbAvailableGroupToolDAO;
import blackboard.data.ValidationException;
import blackboard.data.course.AvailableGroupTool;
import blackboard.data.course.GroupMembership;
import blackboard.data.navigation.NavigationApplication;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Shane Argo on 17/06/2014.
 */
public class BbAvailableGroupToolService {

    @Autowired
    private BbAvailableGroupToolDAO bbAvailableGroupToolDAO;

    private LoadingCache</*Course Id*/Id, ConcurrentMap</*Group Id*/Id, ConcurrentMap</*Avail Group Tool Id*/Id, AvailableGroupTool>>> toolCache;

    public BbAvailableGroupToolService() {
        this(10);
    }

    public BbAvailableGroupToolService(int cacheSize) {
        toolCache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(new CacheLoader<Id, ConcurrentMap<Id, ConcurrentMap<Id, AvailableGroupTool>>>() {
            @Override
            public ConcurrentMap<Id, ConcurrentMap<Id, AvailableGroupTool>> load(Id courseId) throws Exception {
                ConcurrentHashMap<Id, ConcurrentMap<Id, AvailableGroupTool>> groupMap = new ConcurrentHashMap<Id, ConcurrentMap<Id, AvailableGroupTool>>();
                Collection<AvailableGroupTool> groupTools = bbAvailableGroupToolDAO.getByCourseId(courseId);
                for (AvailableGroupTool groupTool : groupTools) {
                    Id groupId = groupTool.getGroupId();
                    ConcurrentMap<Id, AvailableGroupTool> toolsMap = groupMap.get(groupId);
                    if (toolsMap == null) {
                        toolsMap = new ConcurrentHashMap<Id, AvailableGroupTool>();
                        groupMap.put(groupId, toolsMap);
                    }
                    toolsMap.put(groupTool.getId(), groupTool);
                }
                return groupMap;
            }
        });
    }

    public Collection<AvailableGroupTool> getByGroupId(Id groupId, Id courseId) throws ExecutionException {
        return toolCache.get(courseId).get(groupId).values();
    }

    public synchronized void createOrUpdate(AvailableGroupTool availableGroupTool, Id courseId) throws PersistenceException, ExecutionException {
        bbAvailableGroupToolDAO.createOrUpdate(availableGroupTool);

        final ConcurrentMap<Id, ConcurrentMap<Id, AvailableGroupTool>> groupMap = toolCache.get(courseId);

        final Id groupId = availableGroupTool.getGroupId();
        ConcurrentMap<Id, AvailableGroupTool> toolsMap = groupMap.get(groupId);
        if (toolsMap == null) {
            toolsMap = new ConcurrentHashMap<Id, AvailableGroupTool>();
            groupMap.put(groupId, toolsMap);
        }
        toolsMap.put(availableGroupTool.getId(), availableGroupTool);
    }

    public void delete(long toolId, Id groupId, Id courseId) throws PersistenceException, ExecutionException {
        delete(getIdFromLong(toolId), groupId, courseId);
    }

    public synchronized void delete(Id toolId, Id groupId, Id courseId) throws PersistenceException, ExecutionException {
        bbAvailableGroupToolDAO.delete(toolId);

        final ConcurrentMap<Id, AvailableGroupTool> toolsMap = toolCache.get(courseId).get(groupId);
        if(toolsMap != null) {
            toolsMap.remove(toolId);
        }
    }

    public Id getIdFromLong(long id) {
        return Id.toId(GroupMembership.DATA_TYPE, id);
    }

    public BbAvailableGroupToolDAO getBbAvailableGroupToolDAO() {
        return bbAvailableGroupToolDAO;
    }

    public void setBbAvailableGroupToolDAO(BbAvailableGroupToolDAO bbAvailableGroupToolDAO) {
        this.bbAvailableGroupToolDAO = bbAvailableGroupToolDAO;
    }
}
