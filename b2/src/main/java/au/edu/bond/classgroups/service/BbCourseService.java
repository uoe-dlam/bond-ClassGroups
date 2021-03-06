package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbCourseDAO;
import blackboard.data.course.Course;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbCourseService implements Cleanable {

    @Autowired
    private BbCourseDAO bbCourseDAO;

    private LoadingCache<String, Course> byEsidCache;
    private LoadingCache<Id, Id> parentIdCache;

    public BbCourseService(String byEsidCacheSpec, String parentIdCacheSpec) {
        byEsidCache = CacheBuilder.from(byEsidCacheSpec).build(new CacheLoader<String, Course>() {
            @Override
            public Course load(String externalSystemId) throws Exception {
                return bbCourseDAO.getByExternalSystemId(externalSystemId);
            }
        });

        parentIdCache = CacheBuilder.from(parentIdCacheSpec).build(new CacheLoader<Id, Id>() {
            @Override
            public Id load(Id childCourseId) throws Exception {
                return bbCourseDAO.getParentCourseId(childCourseId);
            }
        });
    }

    public Course getByExternalSystemId(String externalSystemId) throws ExecutionException {
        return byEsidCache.get(externalSystemId);
    }

    public Id getParentId(Id childId) throws ExecutionException {
        return parentIdCache.get(childId);
    }

    public synchronized void clearCaches() {
        byEsidCache.invalidateAll();
        byEsidCache.cleanUp();
        parentIdCache.invalidateAll();
        parentIdCache.cleanUp();
    }

    public Id getIdFromLong(Long id) {
        return Id.toId(Course.DATA_TYPE, id);
    }

    public BbCourseDAO getBbCourseDAO() {
        return bbCourseDAO;
    }

    public void setBbCourseDAO(BbCourseDAO bbCourseDAO) {
        this.bbCourseDAO = bbCourseDAO;
    }
}
