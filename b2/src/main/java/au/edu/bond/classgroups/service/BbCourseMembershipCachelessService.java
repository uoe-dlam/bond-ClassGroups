package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbCourseMembershipDAO;
import blackboard.data.ValidationException;
import blackboard.data.course.CourseMembership;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Created by shane on 20/01/15.
 */
public class BbCourseMembershipCachelessService implements BbCourseMembershipService {

    @Autowired
    private BbCourseMembershipDAO bbCourseMembershipDAO;

    @Override
    public CourseMembership getById(long memberId, Id courseId) throws PersistenceException {
        return getById(Id.toId(CourseMembership.DATA_TYPE, memberId), courseId);
    }

    @Override
    public CourseMembership getById(Id courseMembershipId, Id courseId) throws PersistenceException {
        Collection<CourseMembership> courseMemberships = bbCourseMembershipDAO.getByCourseId(courseId);
        for(CourseMembership courseMembership : courseMemberships) {
            if(courseMembership.getId().equals(courseMembershipId)) {
                return courseMembership;
            }
        }
        return null;
    }

    @Override
    public CourseMembership getByCourseIdAndUserId(Id courseId, Id userId) throws PersistenceException {
        Collection<CourseMembership> courseMemberships = bbCourseMembershipDAO.getByCourseId(courseId);
        for(CourseMembership courseMembership : courseMemberships) {
            if(courseMembership.getUserId().equals(userId)) {
                return courseMembership;
            }
        }
        return null;
    }

    @Override
    public void persistCourseMembership(CourseMembership courseMembership) throws ValidationException, PersistenceException {
        bbCourseMembershipDAO.persist(courseMembership);
    }

    public BbCourseMembershipDAO getBbCourseMembershipDAO() {
        return bbCourseMembershipDAO;
    }

    public void setBbCourseMembershipDAO(BbCourseMembershipDAO bbCourseMembershipDAO) {
        this.bbCourseMembershipDAO = bbCourseMembershipDAO;
    }
}
