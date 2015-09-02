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

    private LoadingCache</*Course Id*/Id, ConcurrentMap</*Group Id*/Id, Collection<AvailableGroupTool>>> toolCache;

    public BbAvailableGroupToolService() {
        this(10);
    }

    public BbAvailableGroupToolService(int cacheSize) {
        toolCache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(new CacheLoader<Id, ConcurrentMap<Id, Collection<AvailableGroupTool>>>() {
            @Override
            public ConcurrentMap<Id, Collection<AvailableGroupTool>> load(Id courseId) throws Exception {
                ConcurrentHashMap<Id, Collection<AvailableGroupTool>> toolMap = new ConcurrentHashMap<Id, Collection<AvailableGroupTool>>();
                Collection<AvailableGroupTool> groupTools = bbAvailableGroupToolDAO.getByCourseId(courseId);
                for (AvailableGroupTool groupTool : groupTools) {
                    Id groupId = groupTool.getGroupId();
                    Collection<AvailableGroupTool> toolsList = toolMap.get(groupId);
                    if (toolsList == null) {
                        toolsList = Collections.synchronizedCollection(new ArrayList<AvailableGroupTool>());
                        toolMap.put(groupId, toolsList);
                    }
                    toolsList.add(groupTool);
                }
                return toolMap;
            }
        });
    }

    public Collection<AvailableGroupTool> getByGroupId(Id groupId, Id courseId) throws ExecutionException {
        return toolCache.get(courseId).get(groupId);
    }

    public synchronized void createOrUpdate(AvailableGroupTool availableGroupTool, Id courseId) throws PersistenceException, ExecutionException {
        bbAvailableGroupToolDAO.createOrUpdate(availableGroupTool);

        final ConcurrentMap<Id, Collection<AvailableGroupTool>> toolMap = toolCache.get(courseId);

        final Id groupId = availableGroupTool.getGroupId();
        Collection<AvailableGroupTool> toolsList = toolMap.get(groupId);
        if (toolsList == null) {
            toolsList = Collections.synchronizedCollection(new ArrayList<AvailableGroupTool>());
            toolMap.put(groupId, toolsList);
        }

        if(!toolsList.contains(availableGroupTool)) {
            toolsList.add(availableGroupTool);
        }
    }

    public void delete(long toolId, Id groupId, Id courseId) throws PersistenceException, ExecutionException {
        delete(getIdFromLong(toolId), groupId, courseId);
    }

    public synchronized void delete(Id toolId, Id groupId, Id courseId) throws PersistenceException, ExecutionException {
        bbAvailableGroupToolDAO.delete(toolId);

        final ConcurrentMap<Id, Collection<AvailableGroupTool>> toolMap = toolCache.get(courseId);

        final Collection<AvailableGroupTool> toolsList = toolMap.get(groupId);
        for (AvailableGroupTool availableGroupTool : toolsList) {
            if(availableGroupTool.getGroupId().equals(toolId)) {
                toolsList.remove(availableGroupTool);
                break;
            }
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
