package au.edu.bond.classgroups.manager;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.groupext.GroupExtension;
import au.edu.bond.classgroups.groupext.GroupExtensionService;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.model.Group;
import au.edu.bond.classgroups.service.*;
import au.edu.bond.classgroups.util.EqualityUtil;
import blackboard.base.FormattedText;
import blackboard.data.ValidationException;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.PkId;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Shane Argo on 4/06/2014.
 */
public class BbGroupManager implements GroupManager {

    @Autowired
    private TaskLogger currentTaskLogger;
    @Autowired
    private BbGroupService bbGroupService;
    @Autowired
    private BbCourseService bbCourseService;
    @Autowired
    private GroupExtensionService groupExtensionService;
    @Autowired
    private BbUserService bbUserService;
    @Autowired
    private BbCourseMembershipService bbCourseMembershipService;
    @Autowired
    private Configuration configuration;
    @Autowired
    private GroupTitleService groupTitleService;
    @Autowired
    private ResourceService resourceService;

    @Override
    public Status syncGroup(Group group) {
        GroupExtension ext = groupExtensionService.getGroupExtensionByExternalId(group.getGroupId());

        if(ext != null && !ext.isSynced()) {
            currentTaskLogger.info(resourceService.getLocalisationString(
                    "bond.classgroups.info.skippingunsyncedgroup",
                    group.getGroupId()));
            return Status.NOSYNC;
        }

        Id courseId;
        try {
            courseId = bbCourseService.getByExternalSystemId(group.getCourseId()).getId();
        } catch (PersistenceException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.cantfindcourse", group.getCourseId()));
            return Status.ERROR;
        }

        boolean extNew = false;
        // If extension data is found, attempt to load the Bb group it references.
        blackboard.data.course.Group bbGroup = null;
        if(ext != null) {
            if(ext.getInternalGroupId() == null) {
                currentTaskLogger.info(resourceService.getLocalisationString(
                        "bond.classgroups.info.existinggroupdeleted", group.getGroupId()));
            } else {
                try {
                    bbGroup = bbGroupService.getById(ext.getInternalGroupId(), courseId);
                } catch (KeyNotFoundException e) {
                    currentTaskLogger.info(resourceService.getLocalisationString(
                            "bond.classgroups.info.existinggroupmissing", group.getGroupId()), e);
                } catch (PersistenceException e) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotload", group.getGroupId()), e);
                    return Status.ERROR;
                }
            }
        } else {
            ext = new GroupExtension();
            ext.setExternalSystemId(group.getGroupId());
            extNew = true;
        }
        boolean extDirty = updateFeedLeader(ext, group, courseId);

        String title = groupTitleService.getGroupTitle(group, ext);
        Status status = Status.UNCHANGED;
        // If no Bb group exists (no extension data, or extension data references a deleted group), create one.
        if(bbGroup == null) {
            bbGroup = new blackboard.data.course.Group();

            bbGroup.setCourseId(courseId);
            bbGroup.setTitle(title);

            FormattedText desc = FormattedText.toFormattedText("");
            bbGroup.setDescription(desc);

            bbGroup.setAllowEditToGroup(false);
            bbGroup.setCustomizable(false);
            bbGroup.setGroupSet(false);
            bbGroup.setSelfEnrolledAllowed(false);

            bbGroup.setIsAvailable(calculateAvailability(group));

            updateGroupSet(bbGroup, group);

            status = Status.CREATED;

            currentTaskLogger.info(resourceService.getLocalisationString(
                    "bond.classgroups.info.creatinggroup", group.getGroupId()));
        } else {
            // Force load the group tools, otherwise the API removes them when persisting.
            bbGroup.getAvailableTools(true);
            currentTaskLogger.info(resourceService.getLocalisationString(
                    "bond.classgroups.info.updatinggroup", group.getGroupId()));

            if(!title.equals(bbGroup.getTitle())) {
                bbGroup.setTitle(title);
                status = Status.UPDATED;
            }

            if(updateGroupSet(bbGroup, group)) {
                status = Status.UPDATED;
            }

            if(configuration.getAvailabilityMode() == Configuration.AvailabilityMode.UPDATE) {
                boolean available = calculateAvailability(group);
                if(bbGroup.getIsAvailable() != available) {
                    bbGroup.setIsAvailable(available);
                    status = Status.UPDATED;
                }
            }
        }

            if(status == Status.CREATED || status == Status.UPDATED) {
                // Persist the group.
                try {
                    bbGroupService.createOrUpdate(bbGroup);
                } catch (ValidationException e) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.groupvalidationerrors", group.getGroupId()), e);
                    return Status.ERROR;
                } catch (PersistenceException e) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.groupbberrors", group.getGroupId()), e);
                    return Status.ERROR;
                }
            }

        Long internalId = ((PkId) bbGroup.getId()).getKey();
        if(!internalId.equals(ext.getInternalGroupId())) {
            ext.setInternalGroupId(internalId);
            extDirty = true;
        }

        if(!group.getTitle().equals(ext.getTitle())) {
            ext.setTitle(group.getTitle());
            extDirty = true;
        }

        if(extNew) {
            groupExtensionService.create(ext);
        } else if (extDirty) {
            groupExtensionService.update(ext);
        }

        return status;
    }

    boolean calculateAvailability(Group group) {
        return (group.getAvailable() != null)? group.getAvailable() :
                        (configuration.getDefaultAvailability() == Configuration.GroupAvailability.AVAILABLE);
    }

    private boolean updateFeedLeader(GroupExtension ext, Group group, Id courseId) {
        User user = null;
        CourseMembership courseMembership = null;

        if(group.getLeaderId() != null) {
            try {
                user = bbUserService.getByExternalSystemId(group.getLeaderId(), courseId);
            } catch (KeyNotFoundException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.cantfindleader",
                        group.getLeaderId(), group.getGroupId()), e);
                return false;
            } catch (PersistenceException e) {
                currentTaskLogger.error(resourceService.getLocalisationString(
                        "bond.classgroups.error.cantfindleaderbberror",
                        group.getLeaderId(), group.getGroupId()), e);
                return false;
            }

            if(user != null) {
                try {
                    courseMembership = bbCourseMembershipService.getByCourseIdAndUserId(courseId, user.getId());
                } catch (KeyNotFoundException e) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.cantfindleadermember",
                            group.getLeaderId(), group.getGroupId()), e);
                    return false;
                } catch (PersistenceException e) {
                    currentTaskLogger.error(resourceService.getLocalisationString(
                            "bond.classgroups.error.cantfindleadermemberbberror",
                            group.getLeaderId(), group.getGroupId()), e);
                    return false;
                }
            }
        }

        Id existingFeedId = null;
        if(ext.getLeaderFeedCourseUserId() != null) {
            existingFeedId = bbCourseMembershipService.getIdFromLong(ext.getLeaderFeedCourseUserId());
        }
        Id courseMembershipId = null;
        if(courseMembership != null) {
            courseMembershipId = courseMembership.getId();
        }
        boolean changed = !EqualityUtil.nullSafeEquals(existingFeedId, courseMembershipId);//(existingFeedId == null ? courseMembershipId != null : !existingFeedId.equals(courseMembershipId));

        if(!changed) {
            return false;
        }

        if(courseMembershipId == null) {
            ext.setLeaderFeedCourseUserId(null);
        } else {
            ext.setLeaderFeedCourseUserId(bbCourseMembershipService.getLongFromId(courseMembership.getId()));
        }

        if(configuration.getLeaderChangedMode() == Configuration.LeaderChangedMode.FEED) {
            ext.setLeaderOverridden(false);
        }

        return true;
    }

    private boolean updateGroupSet(blackboard.data.course.Group bbGroup, Group group) {
        if(bbGroup.getSetId() == null && group.getGroupSet() == null) {
            return false;
        }

        if(group.getGroupSet() == null) {
            bbGroup.setSetId(null);
            return true;
        }

        blackboard.data.course.Group bbGroupSet = getOrCreateGroupSet(group.getGroupSet(), bbGroup.getCourseId());
        if(bbGroupSet != null && !EqualityUtil.nullSafeEquals(bbGroupSet.getId(), bbGroup.getSetId())) {
            bbGroup.setSetId(bbGroupSet.getId());
            return true;
        }

        return false;
    }

    private blackboard.data.course.Group getOrCreateGroupSet(String title, Id courseId) {
        blackboard.data.course.Group bbGroupSet = null;
        try {
            bbGroupSet = bbGroupService.getByTitleAndCourseId(title, courseId);
        } catch (PersistenceException ignored) {
        }

        if(bbGroupSet == null) {
            bbGroupSet = new blackboard.data.course.Group();
            bbGroupSet.setGroupSet(true);
            bbGroupSet.setTitle(title);
            bbGroupSet.setCourseId(courseId);

            FormattedText desc = FormattedText.toFormattedText("");
            bbGroupSet.setDescription(desc);

            bbGroupSet.setIsAvailable(false);
            bbGroupSet.setAllowEditToGroup(false);
            bbGroupSet.setSelfEnrolledAllowed(false);

            try {
                bbGroupService.createOrUpdate(bbGroupSet);
            } catch (ValidationException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.groupsetvalidationerrors", title, courseId), e);
                return null;
            } catch (PersistenceException e) {
                currentTaskLogger.error(resourceService.getLocalisationString(
                        "bond.classgroups.error.groupsetbberror", title, courseId), e);
                return null;
            }
        }

        return bbGroupSet;
    }

    public TaskLogger getCurrentTaskLogger() {
        return currentTaskLogger;
    }

    public void setCurrentTaskLogger(TaskLogger currentTaskLogger) {
        this.currentTaskLogger = currentTaskLogger;
    }

    public BbGroupService getBbGroupService() {
        return bbGroupService;
    }

    public void setBbGroupService(BbGroupService bbGroupService) {
        this.bbGroupService = bbGroupService;
    }

    public GroupExtensionService getGroupExtensionService() {
        return groupExtensionService;
    }

    public void setGroupExtensionService(GroupExtensionService groupExtensionService) {
        this.groupExtensionService = groupExtensionService;
    }

    public BbCourseService getBbCourseService() {
        return bbCourseService;
    }

    public void setBbCourseService(BbCourseService bbCourseService) {
        this.bbCourseService = bbCourseService;
    }

    public BbUserService getBbUserService() {
        return bbUserService;
    }

    public void setBbUserService(BbUserService bbUserService) {
        this.bbUserService = bbUserService;
    }

    public BbCourseMembershipService getBbCourseMembershipService() {
        return bbCourseMembershipService;
    }

    public void setBbCourseMembershipService(BbCourseMembershipService bbCourseMembershipService) {
        this.bbCourseMembershipService = bbCourseMembershipService;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public GroupTitleService getGroupTitleService() {
        return groupTitleService;
    }

    public void setGroupTitleService(GroupTitleService groupTitleService) {
        this.groupTitleService = groupTitleService;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
}
