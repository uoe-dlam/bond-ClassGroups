package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbAvailableGroupToolDAO;
import blackboard.data.ValidationException;
import blackboard.data.course.AvailableGroupTool;
import blackboard.data.course.GroupMembership;
import blackboard.data.navigation.NavigationApplication;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Shane Argo on 17/06/2014.
 */
public class BbAvailableGroupToolService {

    @Autowired
    private BbAvailableGroupToolDAO bbAvailableGroupToolDAO;

    private Map<Id, AvailableGroupTool> idCache = new HashMap<Id, AvailableGroupTool>();
    Map</*Group Id*/Id, Map</*Available Group Tool Id*/Id, AvailableGroupTool>> toolCache = new HashMap<Id, Map<Id, AvailableGroupTool>>();

    public Collection<AvailableGroupTool> getByGroupId(Id groupId) throws PersistenceException {
        Collection<AvailableGroupTool> availableGroupTools = null;
        Map<Id, AvailableGroupTool> toolsMap = toolCache.get(groupId);
        if(toolsMap != null) {
            availableGroupTools = toolsMap.values();
        } else {
            availableGroupTools = bbAvailableGroupToolDAO.getByGroupId(groupId);
            for(AvailableGroupTool tool : availableGroupTools) {
                cache(tool);
            }
        }
        return availableGroupTools;
    }

    public Collection<AvailableGroupTool> getByGroupId(Id groupId, Id courseId) throws PersistenceException {
        Collection<AvailableGroupTool> availableGroupTools = null;
        Map<Id, AvailableGroupTool> toolsMap = toolCache.get(groupId);
        if(toolsMap != null) {
            availableGroupTools = toolsMap.values();
        } else {
            availableGroupTools = new HashSet<AvailableGroupTool>();
            Collection<AvailableGroupTool> courseTools = bbAvailableGroupToolDAO.getByCourseId(courseId);
            for(AvailableGroupTool courseTool : courseTools) {
                cache(courseTool);
                if(courseTool.getGroupId().equals(groupId)) {
                    availableGroupTools.add(courseTool);
                }
            }
        }
        return availableGroupTools;
    }

    public void createOrUpdate(AvailableGroupTool availableGroupTool) throws PersistenceException {
        bbAvailableGroupToolDAO.createOrUpdate(availableGroupTool);
        cache(availableGroupTool);
    }

    public void delete(long id) throws PersistenceException {
        delete(getIdFromLong(id));
    }

    public void delete(Id id) throws PersistenceException {
        bbAvailableGroupToolDAO.delete(id);
        uncache(id);
    }

    public Id getIdFromLong(long id) {
        return Id.toId(GroupMembership.DATA_TYPE, id);
    }

    private void cache(AvailableGroupTool availableGroupTool) {
        Id groupId = availableGroupTool.getGroupId();
        Map<Id, AvailableGroupTool> toolMap = toolCache.get(availableGroupTool.getGroupId());
        if(toolMap == null) {
            toolMap = new HashMap<Id, AvailableGroupTool>();
            toolCache.put(groupId, toolMap);
        }
        toolMap.put(availableGroupTool.getId(), availableGroupTool);
        idCache.put(availableGroupTool.getId(), availableGroupTool);
    }

    private void uncache(Id id) {
        AvailableGroupTool availableGroupTool = idCache.get(id);
        if(availableGroupTool != null) {
            idCache.remove(id);
            Map<Id, AvailableGroupTool> toolMap = toolCache.get(availableGroupTool.getGroupId());
            if (toolMap != null) {
                toolCache.remove(availableGroupTool.getId());
            }
        }
    }

    public BbAvailableGroupToolDAO getBbAvailableGroupToolDAO() {
        return bbAvailableGroupToolDAO;
    }

    public void setBbAvailableGroupToolDAO(BbAvailableGroupToolDAO bbAvailableGroupToolDAO) {
        this.bbAvailableGroupToolDAO = bbAvailableGroupToolDAO;
    }
}
