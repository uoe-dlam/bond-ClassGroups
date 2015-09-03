package au.edu.bond.classgroups.manager;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.groupext.GroupExtension;
import au.edu.bond.classgroups.groupext.GroupExtensionService;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.model.Group;
import au.edu.bond.classgroups.model.Member;
import au.edu.bond.classgroups.service.*;
import blackboard.data.ValidationException;
import blackboard.data.course.CourseMembership;
import blackboard.data.course.GroupMembership;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.PkId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Shane Argo on 4/06/2014.
 */
public class BbMemberManager implements MemberManager {

    @Autowired
    private TaskLogger currentTaskLogger;
    @Autowired
    private BbGroupMembershipService bbGroupMembershipService;
    @Autowired
    private GroupExtensionService groupExtensionService;
    @Autowired
    private BbCourseService bbCourseService;
    @Autowired
    private BbUserService bbUserService;
    @Autowired
    private BbGroupService bbGroupService;
    @Autowired
    private BbCourseMembershipService bbCourseMembershipService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private Configuration configuration;

    public void syncMembers(Group group) {
        Id courseId;
        try {
            courseId = bbCourseService.getByExternalSystemId(group.getCourseId()).getId();
        } catch (ExecutionException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.cantfindcourse", group.getCourseId()));
            return;
        }

        GroupExtension ext;
        try {
            ext = groupExtensionService.getGroupExtensionByExternalId(group.getGroupId(), ((PkId)courseId).getKey());
        } catch (ExecutionException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.cantloadextension", group.getCourseId()));
            return;
        }
        Id groupId = bbGroupService.getIdFromLong(ext.getInternalGroupId());

        Collection<GroupMembership> deleteMembers;
        try {
            deleteMembers = bbGroupMembershipService.getByGroupId(groupId, courseId);
        } catch (ExecutionException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.couldnotloadmembers",
                    group.getCourseId()), e);
            return;
        }

        Id leaderCourseMembershipId = null;
        if(ext.calculateLeaderCourseUserId() != null) {
            leaderCourseMembershipId = Id.toId(CourseMembership.DATA_TYPE, ext.calculateLeaderCourseUserId());
        }

        Map<GroupMembership, Member> createMembers = new HashMap<GroupMembership, Member>();
        if(group.getMembers() != null) {
            for (Member member : group.getMembers()) {

                User user;
                try {
                    user = bbUserService.getByExternalSystemId(member.getUserId(), courseId);
                } catch (ExecutionException e) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotloaduser",
                            member.getUserId(), group.getGroupId(), group.getCourseId(), group.getTitle()), e);
                    continue;
                }

                if (user == null) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotloaduser",
                            member.getUserId(), group.getGroupId(), group.getCourseId(), group.getTitle()));
                    continue;
                }

                CourseMembership courseMembership;
                try {
                    courseMembership = bbCourseMembershipService.getByCourseIdAndUserId(courseId, user.getId());
                } catch (ExecutionException e) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotloadcousemember",
                            group.getCourseId(), member.getUserId(), group.getGroupId(), group.getTitle()), e);
                    continue;
                }

                if (courseMembership == null) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotloadcousemember",
                            group.getCourseId(), member.getUserId(), group.getGroupId(), group.getTitle()));
                    continue;
                }

                if (courseMembership.getId().equals(leaderCourseMembershipId)) {
                    currentTaskLogger.info(resourceService.getLocalisationString(
                            "bond.classgroups.warning.skippingmemberwhoisleader",
                            member.getUserId(), group.getGroupId(), group.getTitle()));
                    continue;
                }

                GroupMembership existingMember = null;
                for (GroupMembership deleteMember : deleteMembers) {
                    if (deleteMember.getCourseMembershipId().equals(courseMembership.getId())) {
                        existingMember = deleteMember;
                        break;
                    }
                }

                if (existingMember == null) {
                    GroupMembership groupMembership = new GroupMembership();
                    groupMembership.setCourseMembershipId(courseMembership.getId());
                    groupMembership.setGroupId(groupId);
                    createMembers.put(groupMembership, member);
                } else {
                    deleteMembers.remove(existingMember);
                }
            }
        }

        boolean leaderFound = false;
        if(leaderCourseMembershipId != null) {
            for (GroupMembership member : deleteMembers) {
                if (member.getCourseMembershipId().equals(leaderCourseMembershipId)) {
                    leaderFound = true;
                    deleteMembers.remove(member);
                    break;
                }
            }
        }

        if(deleteMembers.size() > 0) {
            currentTaskLogger.info(resourceService.getLocalisationString(
                    "bond.classgroups.info.deletingmembers",
                    deleteMembers.size(), group.getGroupId(), group.getTitle()));
        }
        for(GroupMembership deleteMember : deleteMembers) {
            try {
                bbGroupMembershipService.delete(deleteMember.getId(), groupId, courseId);
            } catch (PersistenceException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotdeletemember",
                        deleteMember.getId(), group.getGroupId()), e);
            } catch (ExecutionException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotdeletememberexecution",
                        deleteMember.getId(), group.getGroupId()), e);
            }
        }

        if(createMembers.size() > 0) {
            currentTaskLogger.info(resourceService.getLocalisationString(
                    "bond.classgroups.info.addingmembers",
                    createMembers.size(), group.getGroupId(), group.getTitle()));
        }
        for(GroupMembership createMember : createMembers.keySet()) {
            try {
                bbGroupMembershipService.createOrUpdate(createMember, courseId);
            } catch (ValidationException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotpersistgroupuservalidationerrors",
                        createMembers.get(createMember).getUserId(), group.getGroupId()), e);
            } catch (PersistenceException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotpersistgroupuserbberrors",
                        createMembers.get(createMember).getUserId(), group.getGroupId()), e);
            } catch (ExecutionException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotpersistgroupuserexecution",
                        createMembers.get(createMember).getUserId(), group.getGroupId()), e);
            }
        }

        if(!leaderFound && leaderCourseMembershipId != null) {
            CourseMembership courseMembership = null;
            try {
                courseMembership = bbCourseMembershipService.getById(leaderCourseMembershipId, courseId);
            } catch (ExecutionException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotfindleadersmembershipbberrors",
                        group.getGroupId()), e);
                return;
            }

            if(courseMembership == null) {
                if(!configuration.isEnrolStaffIfMissing()) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotfindleadersmembership",
                            group.getGroupId()));
                    return;
                } else {
                    courseMembership = new CourseMembership();
                    courseMembership.setCourseId(courseId);
                    courseMembership.setUserId(leaderCourseMembershipId);
                    try {
                        bbCourseMembershipService.persistCourseMembership(courseMembership);
                    } catch (ValidationException e) {
                        currentTaskLogger.warning(resourceService.getLocalisationString(
                                "bond.classgroups.warning.couldnotpersistleadercoursemembershipvalidationerrors",
                                leaderCourseMembershipId.getExternalString(), group.getGroupId()), e);
                        return;
                    } catch (PersistenceException e) {
                        currentTaskLogger.warning(resourceService.getLocalisationString(
                                "bond.classgroups.warning.couldnotpersistleadercoursemembershipbberrors",
                                leaderCourseMembershipId.getExternalString(), group.getGroupId()), e);
                        return;
                    } catch (ExecutionException e) {
                        currentTaskLogger.warning(resourceService.getLocalisationString(
                                "bond.classgroups.warning.couldnotpersistleadercoursemembershipexecution",
                                leaderCourseMembershipId.getExternalString(), group.getGroupId()), e);
                        return;
                    }
                }
            }

            User leader = null;
            try {
                leader = bbUserService.getById(courseMembership.getUserId(), courseId);
            } catch(ExecutionException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotaddleadernotfound",
                        group.getGroupId()), e);
                return;
            }

            currentTaskLogger.info(resourceService.getLocalisationString(
                    "bond.classgroups.info.addingleader",
                    leader.getBatchUid(), group.getGroupId(), group.getTitle()));
            GroupMembership leaderMembership = new GroupMembership();
            leaderMembership.setCourseMembershipId(leaderCourseMembershipId);
            leaderMembership.setGroupId(groupId);
            try {
                bbGroupMembershipService.createOrUpdate(leaderMembership, courseId);
            } catch (ValidationException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotaddleadervalidationerrors",
                        leader.getBatchUid(), group.getGroupId()), e);
            } catch (PersistenceException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotaddleaderbberrors",
                        leader.getBatchUid(), group.getGroupId()), e);
            } catch (ExecutionException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotaddleaderexecution",
                        leader.getBatchUid(), group.getGroupId()), e);
            }
        }

    }

    public TaskLogger getCurrentTaskLogger() {
        return currentTaskLogger;
    }

    public void setCurrentTaskLogger(TaskLogger currentTaskLogger) {
        this.currentTaskLogger = currentTaskLogger;
    }

    public BbGroupMembershipService getBbGroupMembershipService() {
        return bbGroupMembershipService;
    }

    public void setBbGroupMembershipService(BbGroupMembershipService bbGroupMembershipService) {
        this.bbGroupMembershipService = bbGroupMembershipService;
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

    public BbGroupService getBbGroupService() {
        return bbGroupService;
    }

    public void setBbGroupService(BbGroupService bbGroupService) {
        this.bbGroupService = bbGroupService;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
