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
public class BbCourseMembershipService {

    @Autowired
    private BbCourseMembershipDAO bbCourseMembershipDAO;

    private LoadingCache</*Course Id*/Id, ConcurrentMap</*User Id*/Id, CourseMembership>> byUserIdCache;
    private LoadingCache</*Course Id*/Id, ConcurrentMap</*CourseMembership Id*/Id, CourseMembership>> byIdCache;

    public BbCourseMembershipService() {
        this(10);
    }

    public BbCourseMembershipService(int cacheSize) {
        byIdCache = CacheBuilder.newBuilder().maximumSize(10).build(new CacheLoader<Id, ConcurrentMap<Id, CourseMembership>>() {
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

        byUserIdCache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(new CacheLoader<Id, ConcurrentMap<Id, CourseMembership>>() {
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
        final Id courseId = courseMembership.getCourseId();
        final ConcurrentMap<Id, CourseMembership> byIdMap = byIdCache.get(courseId);
        final ConcurrentMap<Id, CourseMembership> byUserIdMap = byUserIdCache.get(courseId);

        byIdMap.put(courseMembership.getId(), courseMembership);
        byUserIdMap.put(courseMembership.getUserId(), courseMembership);

        bbCourseMembershipDAO.persist(courseMembership);
    }

    public BbCourseMembershipDAO getBbCourseMembershipDAO() {
        return bbCourseMembershipDAO;
    }

    public void setBbCourseMembershipDAO(BbCourseMembershipDAO bbCourseMembershipDAO) {
        this.bbCourseMembershipDAO = bbCourseMembershipDAO;
    }
}
