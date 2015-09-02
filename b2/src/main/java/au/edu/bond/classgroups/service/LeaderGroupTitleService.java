package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbCourseMembershipDAO;
import au.edu.bond.classgroups.dao.BbUserDAO;
import au.edu.bond.classgroups.groupext.GroupExtension;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Shane Argo on 20/06/2014.
 */
public class LeaderGroupTitleService implements GroupTitleService {

    @Autowired
    private BbCourseMembershipDAO bbCourseMembershipDAO;
    @Autowired
    private BbUserDAO bbUserDAO;
    @Autowired
    private ResourceService resourceService;

    public String getGroupTitle(String baseTitle, GroupExtension extension) {
        Long leaderCourseMembershipId = extension.calculateLeaderCourseUserId();
        if(leaderCourseMembershipId == null) {
            return baseTitle;
        }

        CourseMembership courseMembership = null;
        try {
            courseMembership = bbCourseMembershipDAO.getById(leaderCourseMembershipId);
        } catch (PersistenceException e) {
            return baseTitle;
        }

        User user = null;
        try {
            user = bbUserDAO.getById(courseMembership.getUserId());
        } catch (PersistenceException e) {
            return baseTitle;
        }

        return resourceService.getLocalisationString("bond.classgroups.pattern.group", baseTitle, user.getGivenName(), user.getFamilyName());
    }

    public BbCourseMembershipDAO getBbCourseMembershipDAO() {
        return bbCourseMembershipDAO;
    }

    public void setBbCourseMembershipDAO(BbCourseMembershipDAO bbCourseMembershipDAO) {
        this.bbCourseMembershipDAO = bbCourseMembershipDAO;
    }

    public BbUserDAO getBbUserDAO() {
        return bbUserDAO;
    }

    public void setBbUserDAO(BbUserDAO bbUserDAO) {
        this.bbUserDAO = bbUserDAO;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
}
