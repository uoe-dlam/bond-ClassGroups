package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbCourseMembershipDAO;
import blackboard.data.ValidationException;
import blackboard.data.course.CourseMembership;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.persist.PkId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbCourseMembershipCachingService implements BbCourseMembershipService {

    @Autowired
    private BbCourseMembershipDAO bbCourseMembershipDAO;

    Map<Id, CourseMembership> idCache = new HashMap<Id, CourseMembership>();

    Map</* CourseId */Id, Map</* UserId */Id, CourseMembership>> courseCache = new HashMap<Id, Map<Id, CourseMembership>>();

    @Override
    public CourseMembership getById(long id) throws PersistenceException {
        return getById(Id.toId(CourseMembership.DATA_TYPE, id));
    }

    @Override
    public CourseMembership getById(Id id) throws PersistenceException {
        CourseMembership membership = idCache.get(id);
        if(membership == null) {
            membership = bbCourseMembershipDAO.getById(id);
            idCache.put(membership.getId(), membership);
        }
        return membership;
    }

    @Override
    public CourseMembership getById(Id courseMembershipId, Id courseId) throws PersistenceException {
        CourseMembership membership = idCache.get(courseMembershipId);
        if(membership == null) {
            Collection<CourseMembership> courseMemberships = bbCourseMembershipDAO.getByCourseId(courseId);
            for(CourseMembership courseMembership : courseMemberships) {
                cache(courseMembership);
                if(courseMembership.getId().equals(courseMembershipId)) {
                    membership = courseMembership;
                    break;
                }
            }
        }
        return membership;
    }

    @Override
    public CourseMembership getByCourseIdAndUserId(Id courseId, Id userId) throws PersistenceException {
        CourseMembership membership = getFromCache(courseId, userId);
        if(membership == null) {
            Collection<CourseMembership> courseMemberships = bbCourseMembershipDAO.getByCourseId(courseId);
            for(CourseMembership courseMembership : courseMemberships) {
                cache(courseMembership);
                if(courseMembership.getUserId().equals(userId)) {
                    membership = courseMembership;
                    break;
                }
            }
        }
        return membership;
    }

    @Override
    public void persistCourseMembership(CourseMembership courseMembership) throws ValidationException, PersistenceException {
        bbCourseMembershipDAO.persist(courseMembership);
        cache(courseMembership);
    }

    private void cache(CourseMembership membership) {
        idCache.put(membership.getId(), membership);
        Map<Id, CourseMembership> cache = courseCache.get(membership.getCourseId());
        if(cache == null) {
            cache = new HashMap<Id, CourseMembership>();
        }
        cache.put(membership.getUserId(), membership);
    }

    private CourseMembership getFromCache(Id courseId, Id userId) {
        Map<Id, CourseMembership> courseMap = courseCache.get(courseId);
        if(courseMap == null) {
            return null;
        }
        return courseMap.get(userId);
    }

    public BbCourseMembershipDAO getBbCourseMembershipDAO() {
        return bbCourseMembershipDAO;
    }

    public void setBbCourseMembershipDAO(BbCourseMembershipDAO bbCourseMembershipDAO) {
        this.bbCourseMembershipDAO = bbCourseMembershipDAO;
    }
}
