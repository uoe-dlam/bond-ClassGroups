package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.groupext.GroupExtension;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.model.Group;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shane Argo on 20/06/2014.
 */
public class LeaderGroupTitleService implements GroupTitleService {

    @Autowired
    private BbCourseMembershipService bbCourseMembershipService;
    @Autowired
    private BbUserService bbUserService;
    @Autowired
    private TaskLogger currentTaskLogger;
    @Autowired
    private ResourceService resourceService;

    public Map<String, String> cache = new HashMap<String, String>();


    public String getGroupTitle(Group group, GroupExtension extension) {
        String name = cache.get(group.getGroupId());
        if(name == null) {
            name = calculateGroupName(group, extension);
            cache.put(group.getGroupId(), name);
        }
        return name;
    }

    private String calculateGroupName(Group group, GroupExtension extension) {

        Long leaderCourseMembershipId = extension.calculateLeaderCourseUserId();
        if(leaderCourseMembershipId == null) {
            return group.getTitle();
        }

        CourseMembership courseMembership = null;
        try {
            courseMembership = bbCourseMembershipService.getById(leaderCourseMembershipId);
        } catch (PersistenceException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.failedtogetleaderforgrouptitle",
                    group.getGroupId()), e);
            return group.getTitle();
        }

        User user = null;
        try {
            user = bbUserService.getById(courseMembership.getUserId());
        } catch (PersistenceException e) {
            currentTaskLogger.warning(resourceService.getLocalisationString(
                    "bond.classgroups.warning.failedtogetleaderforgrouptitle",
                    group.getGroupId()), e);
            return group.getTitle();
        }

        return resourceService.getLocalisationString("bond.classgroups.pattern.group", group.getTitle(), user.getGivenName(), user.getFamilyName());
    }

    public TaskLogger getCurrentTaskLogger() {
        return currentTaskLogger;
    }

    public void setCurrentTaskLogger(TaskLogger currentTaskLogger) {
        this.currentTaskLogger = currentTaskLogger;
    }

    public BbCourseMembershipService getBbCourseMembershipService() {
        return bbCourseMembershipService;
    }

    public void setBbCourseMembershipService(BbCourseMembershipService bbCourseMembershipService) {
        this.bbCourseMembershipService = bbCourseMembershipService;
    }

    public BbUserService getBbUserService() {
        return bbUserService;
    }

    public void setBbUserService(BbUserService bbUserService) {
        this.bbUserService = bbUserService;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
}
