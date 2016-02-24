package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbCourseMembershipDAO;
import au.edu.bond.classgroups.dao.BbUserDAO;
import au.edu.bond.classgroups.groupext.GroupExtension;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

/**
 * Created by Shane Argo on 20/06/2014.
 */
public class LeaderGroupTitleService implements GroupTitleService {

    @Autowired
    private BbCourseMembershipService bbCourseMembershipService;
    @Autowired
    private BbUserService bbUserService;
    @Autowired
    private BbGroupService bbGroupService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private BbCourseService bbCourseService;

    public String getGroupTitle(String baseTitle, GroupExtension extension, long courseId) {

        Long leaderCourseMembershipId = extension.calculateLeaderCourseUserId();
        if(leaderCourseMembershipId == null) {
            return baseTitle;
        }

        final Id courseIdObj = bbCourseService.getIdFromLong(courseId);

        CourseMembership courseMembership = null;
        try {
            courseMembership = bbCourseMembershipService.getById(leaderCourseMembershipId, courseIdObj);
        } catch (Exception e) {
            return baseTitle;
        }

        User user = null;
        try {
            user = bbUserService.getById(courseMembership.getUserId(), courseIdObj);
        } catch (Exception e) {
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
