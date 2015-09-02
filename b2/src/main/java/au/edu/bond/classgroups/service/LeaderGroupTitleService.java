package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.groupext.GroupExtension;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Shane Argo on 20/06/2014.
 */
public class LeaderGroupTitleService implements GroupTitleService {

    private BbCourseMembershipService bbCourseMembershipService;
    private BbUserService bbUserService;
    @Autowired
    private ResourceService resourceService;

    public String getGroupTitle(String baseTitle, GroupExtension extension) {
        Long leaderCourseMembershipId = extension.calculateLeaderCourseUserId();
        if(leaderCourseMembershipId == null) {
            return baseTitle;
        }

        

        CourseMembership courseMembership = null;
        try {
            courseMembership = bbCourseMembershipService.getById(leaderCourseMembershipId);
        } catch (PersistenceException e) {
            return baseTitle;
        }

        User user = null;
        try {
            user = bbUserService.getById(courseMembership.getUserId());
        } catch (PersistenceException e) {
            return baseTitle;
        }

        return resourceService.getLocalisationString("bond.classgroups.pattern.group", baseTitle, user.getGivenName(), user.getFamilyName());
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
