package au.edu.bond.classgroups.manager;

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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    private BbUserCachingService bbUserCachingService;
    @Autowired
    private BbGroupService bbGroupService;
    @Autowired
    private BbCourseMembershipCachingService bbCourseMembershipCachingService;
    @Autowired
    private ResourceService resourceService;

    public void syncMembers(Group group) {
        GroupExtension ext = groupExtensionService.getGroupExtensionByExternalId(group.getGroupId());
        Id groupId = bbGroupService.getIdFromLong(ext.getInternalGroupId());

        Id courseId;
        try {
            courseId = bbCourseService.getByExternalSystemId(group.getCourseId()).getId();
        } catch (PersistenceException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.cantfindcourse", group.getCourseId()));
            return;
        }

        Collection<GroupMembership> deleteMembers;
        try {
            deleteMembers = bbGroupMembershipService.getByGroupId(groupId, courseId);
        } catch (KeyNotFoundException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.cantlocateexistingmembers",
                    ext.getExternalSystemId()), e);
            return;
        } catch (PersistenceException e) {
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
                    user = bbUserCachingService.getByExternalSystemId(member.getUserId(), courseId);
                } catch (PersistenceException e) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotloaduser",
                            member.getUserId(), group.getGroupId()), e);
                    continue;
                }

                if (user == null) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotloaduser",
                            member.getUserId(), group.getGroupId()));
                    continue;
                }

                CourseMembership courseMembership;
                try {
                    courseMembership = bbCourseMembershipCachingService.getByCourseIdAndUserId(courseId, user.getId());
                } catch (PersistenceException e) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotloadcousemember",
                            group.getCourseId(), member.getUserId()), e);
                    continue;
                }

                if (courseMembership == null) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.couldnotloadcousemember",
                            group.getCourseId(), member.getUserId()));
                    continue;
                }

                if (courseMembership.getId().equals(leaderCourseMembershipId)) {
                    currentTaskLogger.info(resourceService.getLocalisationString(
                            "bond.classgroups.warning.skippingmemberwhoisleader",
                            member.getUserId(), group.getGroupId()));
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
                    deleteMembers.size(), group.getGroupId()));
        }
        for(GroupMembership deleteMember : deleteMembers) {
            try {
                bbGroupMembershipService.delete(deleteMember.getId());
            } catch (PersistenceException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotdeletemember",
                        deleteMember.getId(), group.getGroupId()), e);
            }
        }

        if(createMembers.size() > 0) {
            currentTaskLogger.info(resourceService.getLocalisationString(
                    "bond.classgroups.info.addingmembers",
                    createMembers.size(), group.getGroupId()));
        }
        for(GroupMembership createMember : createMembers.keySet()) {
            try {
                bbGroupMembershipService.createOrUpdate(createMember);
            } catch (ValidationException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotpersistgroupuservalidationerrors",
                        createMembers.get(createMember).getUserId(), group.getGroupId()), e);
            } catch (PersistenceException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotpersistgroupuserbberrors",
                        createMembers.get(createMember).getUserId(), group.getGroupId()), e);
            }
        }

        if(!leaderFound && leaderCourseMembershipId != null) {
            CourseMembership courseMembership = null;
            try {
                courseMembership = bbCourseMembershipCachingService.getById(leaderCourseMembershipId);
            } catch(KeyNotFoundException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotfindleadersmembership",
                        group.getGroupId()), e);
                return;
            } catch (PersistenceException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotfindleadersmembershipbberrors",
                        group.getGroupId()), e);
                return;
            }

            User leader = null;
            try {
                leader = bbUserCachingService.getById(courseMembership.getUserId());
            } catch(KeyNotFoundException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotaddleadernotfound",
                        group.getGroupId()), e);
                return;
            } catch (PersistenceException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotaddleadermembershipbberrors",
                        group.getGroupId()), e);
                return;
            }

            currentTaskLogger.info(resourceService.getLocalisationString(
                    "bond.classgroups.info.addingleader",
                    leader.getBatchUid(), group.getGroupId()));
            GroupMembership leaderMembership = new GroupMembership();
            leaderMembership.setCourseMembershipId(leaderCourseMembershipId);
            leaderMembership.setGroupId(groupId);
            try {
                bbGroupMembershipService.createOrUpdate(leaderMembership);
            } catch (ValidationException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotaddleadervalidationerrors",
                        leader.getBatchUid(), group.getGroupId()), e);
            } catch (PersistenceException e) {
                currentTaskLogger.warning(resourceService.getLocalisationString(
                        "bond.classgroups.warning.couldnotaddleaderbberrors",
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

    public BbUserCachingService getBbUserCachingService() {
        return bbUserCachingService;
    }

    public void setBbUserCachingService(BbUserCachingService bbUserCachingService) {
        this.bbUserCachingService = bbUserCachingService;
    }

    public BbCourseMembershipCachingService getBbCourseMembershipCachingService() {
        return bbCourseMembershipCachingService;
    }

    public void setBbCourseMembershipCachingService(BbCourseMembershipCachingService bbCourseMembershipCachingService) {
        this.bbCourseMembershipCachingService = bbCourseMembershipCachingService;
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
}
