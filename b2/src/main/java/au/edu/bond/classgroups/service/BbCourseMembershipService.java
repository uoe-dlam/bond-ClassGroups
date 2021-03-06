package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbCourseMembershipDAO;
import blackboard.data.ValidationException;
import blackboard.data.course.CourseMembership;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbCourseMembershipService implements Cleanable {

    @Autowired
    private BbCourseMembershipDAO bbCourseMembershipDAO;

    private LoadingCache</*Course Id*/Id, ConcurrentMap</*User Id*/Id, CourseMembership>> byUserIdCache;
    private LoadingCache</*Course Id*/Id, ConcurrentMap</*CourseMembership Id*/Id, CourseMembership>> byIdCache;

    public BbCourseMembershipService(String byIdCacheSpec, String byUserIdCacheSpec) {
        byIdCache = CacheBuilder.from(byIdCacheSpec).build(new CacheLoader<Id, ConcurrentMap<Id, CourseMembership>>() {
            @Override
            public ConcurrentMap<Id, CourseMembership> load(Id courseId) throws Exception {
                final Collection<CourseMembership> memberships = bbCourseMembershipDAO.getByCourseId(courseId);
                final ConcurrentHashMap<Id, CourseMembership> memberMap = new ConcurrentHashMap<Id, CourseMembership>();
                for (CourseMembership membership : memberships) {
                    memberMap.put(membership.getId(), membership);
                }
                return memberMap;
            }
        });

        byUserIdCache = CacheBuilder.from(byUserIdCacheSpec).build(new CacheLoader<Id, ConcurrentMap<Id, CourseMembership>>() {
            @Override
            public ConcurrentMap<Id, CourseMembership> load(Id courseId) throws Exception {
                final Collection<CourseMembership> memberships = byIdCache.get(courseId).values();
                final ConcurrentHashMap<Id, CourseMembership> memberMap = new ConcurrentHashMap<Id, CourseMembership>();
                for (CourseMembership membership : memberships) {
                    memberMap.put(membership.getUserId(), membership);
                }
                return memberMap;
            }
        });
    }

    public CourseMembership getById(long id, Id courseId) throws ExecutionException {
        return getById(Id.toId(CourseMembership.DATA_TYPE, id), courseId);
    }

    public CourseMembership getById(Id membershipId, Id courseId) throws ExecutionException {
        return byIdCache.get(courseId).get(membershipId);
    }

    public CourseMembership getByCourseIdAndUserId(Id courseId, Id userId) throws ExecutionException {
        return byUserIdCache.get(courseId).get(userId);
    }

    public synchronized void persistCourseMembership(CourseMembership courseMembership) throws ValidationException, PersistenceException, ExecutionException {
        bbCourseMembershipDAO.persist(courseMembership);

        final Id courseId = courseMembership.getCourseId();
        final ConcurrentMap<Id, CourseMembership> byIdMap = byIdCache.get(courseId);
        final ConcurrentMap<Id, CourseMembership> byUserIdMap = byUserIdCache.get(courseId);

        byIdMap.put(courseMembership.getId(), courseMembership);
        byUserIdMap.put(courseMembership.getUserId(), courseMembership);
    }

    public synchronized void clearCaches() {
        byUserIdCache.invalidateAll();
        byUserIdCache.cleanUp();
        byIdCache.invalidateAll();
        byIdCache.cleanUp();
    }

    public BbCourseMembershipDAO getBbCourseMembershipDAO() {
        return bbCourseMembershipDAO;
    }

    public void setBbCourseMembershipDAO(BbCourseMembershipDAO bbCourseMembershipDAO) {
        this.bbCourseMembershipDAO = bbCourseMembershipDAO;
    }
}
