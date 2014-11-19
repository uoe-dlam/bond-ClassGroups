package au.edu.bond.classgroups.manager;

import au.edu.bond.classgroups.config.model.Configuration;
import au.edu.bond.classgroups.groupext.GroupExtension;
import au.edu.bond.classgroups.groupext.GroupExtensionService;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.model.Group;
import au.edu.bond.classgroups.service.BbAvailableGroupToolService;
import au.edu.bond.classgroups.service.BbCourseService;
import au.edu.bond.classgroups.service.BbGroupService;
import au.edu.bond.classgroups.service.ResourceService;
import blackboard.data.ValidationException;
import blackboard.data.course.AvailableGroupTool;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by Shane Argo on 17/06/2014.
 */
public class BbToolManager implements ToolManager {

    @Autowired
    private TaskLogger currentTaskLogger;
    @Autowired
    private GroupExtensionService groupExtensionService;
    @Autowired
    private BbGroupService bbGroupService;
    @Autowired
    private BbAvailableGroupToolService bbAvailableGroupToolService;
    @Autowired
    private BbCourseService bbCourseService;
    @Autowired
    private Configuration configuration;
    @Autowired
    private ResourceService resourceService;

    @Override
    public void syncGroupTools(Group group) {
        GroupExtension ext = groupExtensionService.getGroupExtensionByExternalId(group.getGroupId());
        Id groupId = bbGroupService.getIdFromLong(ext.getInternalGroupId());

        Id courseId;
        try {
            courseId = bbCourseService.getByExternalSystemId(group.getCourseId()).getId();
        } catch (PersistenceException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.cantfindcourse",
                    group.getCourseId()));
            return;
        }

        Collection<AvailableGroupTool> deleteTools;
        try {
            deleteTools = bbAvailableGroupToolService.getByGroupId(groupId, courseId);
        } catch (KeyNotFoundException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.couldnotfindexistingtools",
                    ext.getExternalSystemId()), e);
            return;
        } catch (PersistenceException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.couldnotfindexistingtoolsbberrors",
                    ext.getExternalSystemId()), e);
            return;
        }

        Collection<String> tools = (group.getTools() != null) ? group.getTools() : configuration.getDefaultTools();

        if(tools != null && tools.size() > 0) {
            Map<AvailableGroupTool, String> createTools = new HashMap<AvailableGroupTool, String>();
            for (String tool : tools) {
                AvailableGroupTool existingTool = null;
                for (AvailableGroupTool deleteTool : deleteTools) {
                    if (deleteTool.getApplicationHandle().equals(tool)) {
                        existingTool = deleteTool;
                        break;
                    }
                }

                if (existingTool == null) {
                    AvailableGroupTool availableGroupTool = new AvailableGroupTool();
                    availableGroupTool.setGroupId(groupId);
                    availableGroupTool.setApplicationHandle(tool);
                    createTools.put(availableGroupTool, tool);
                } else {
                    deleteTools.remove(existingTool);
                }
            }

            if(createTools.size() > 0) {
                currentTaskLogger.info(resourceService.getLocalisationString(
                        "bond.classgroups.info.addingtools",
                        createTools.size(), group.getGroupId()));
            }
            for(AvailableGroupTool createTool : createTools.keySet()) {
                try {
                    bbAvailableGroupToolService.createOrUpdate(createTool);
                } catch (PersistenceException e) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotpersistgrouptool",
                            createTools.get(createTool), group.getGroupId()), e);
                    return;
                }
            }
        }

        if(configuration.getToolsMode() == Configuration.ToolsMode.SYNC) {
            if (deleteTools.size() > 0) {
                currentTaskLogger.info(resourceService.getLocalisationString(
                        "bond.classgroups.info.deletingtools",
                        deleteTools.size(), group.getGroupId()));
            }
            for (AvailableGroupTool deleteTool : deleteTools) {
                try {
                    bbAvailableGroupToolService.delete(deleteTool.getId());
                } catch (PersistenceException e) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotremovegrouptool",
                            deleteTool.getId(), group.getGroupId()), e);
                }
            }
        }
    }

    public TaskLogger getCurrentTaskLogger() {
        return currentTaskLogger;
    }

    public void setCurrentTaskLogger(TaskLogger currentTaskLogger) {
        this.currentTaskLogger = currentTaskLogger;
    }

    public GroupExtensionService getGroupExtensionService() {
        return groupExtensionService;
    }

    public void setGroupExtensionService(GroupExtensionService groupExtensionService) {
        this.groupExtensionService = groupExtensionService;
    }

    public BbGroupService getBbGroupService() {
        return bbGroupService;
    }

    public void setBbGroupService(BbGroupService bbGroupService) {
        this.bbGroupService = bbGroupService;
    }

    public BbAvailableGroupToolService getBbAvailableGroupToolService() {
        return bbAvailableGroupToolService;
    }

    public void setBbAvailableGroupToolService(BbAvailableGroupToolService bbAvailableGroupToolService) {
        this.bbAvailableGroupToolService = bbAvailableGroupToolService;
    }

    public BbCourseService getBbCourseService() {
        return bbCourseService;
    }

    public void setBbCourseService(BbCourseService bbCourseService) {
        this.bbCourseService = bbCourseService;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
