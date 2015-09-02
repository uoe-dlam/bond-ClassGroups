package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbGroupDAO;
import blackboard.data.ValidationException;
import blackboard.data.course.Group;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.IDN;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbGroupService {

    @Autowired
    private BbGroupDAO bbGroupDAO;

    private LoadingCache</*Course Id*/Id, ConcurrentMap</*Group Title*/String, Group>> titleCache;
    private LoadingCache</*Course Id*/Id, ConcurrentMap</*Group Id*/Id, Group>> groupCache;

    public BbGroupService() {
        this(10);
    }

    public BbGroupService(int cacheSize) {
        groupCache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(new CacheLoader<Id, ConcurrentMap<Id, Group>>() {
            @Override
            public ConcurrentMap<Id, Group> load(Id courseId) throws Exception {
                Collection<Group> classGroups = bbGroupDAO.getByCourseId(courseId);
                ConcurrentHashMap<Id, Group> groupMap = new ConcurrentHashMap<Id, Group>();
                for (Group group : classGroups) {
                    groupMap.put(group.getId(), group);
                }
                return groupMap;
            }
        });

        titleCache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(new CacheLoader<Id, ConcurrentMap<String, Group>>() {
            @Override
            public ConcurrentMap<String, Group> load(Id courseId) throws Exception {
                Map<Id, Group> groupMap = groupCache.get(courseId);
                ConcurrentHashMap<String, Group> titleMap = new ConcurrentHashMap<String, Group>();
                for (Group group : groupMap.values()) {
                    titleMap.put(group.getTitle(), group);
                }
                return titleMap;
            }
        });
    }

    public Group getById(long id, Id courseId) throws ExecutionException {
        return getById(getIdFromLong(id), courseId);
    }

    public Group getById(Id groupId, Id courseId) throws ExecutionException {
        return groupCache.get(courseId).get(groupId);
    }

    public Collection<Group> getByCourseId(Id courseId) throws ExecutionException {
        return groupCache.get(courseId).values();
    }

    public Group getByTitleAndCourseId(String title, Id courseId) throws ExecutionException {
        return titleCache.get(courseId).get(title);
    }

    public synchronized void createOrUpdate(Group group) throws ValidationException, PersistenceException, ExecutionException {
        Id courseId = group.getCourseId();
        Map<Id, Group> groupMap = groupCache.get(courseId);
        Map<String, Group> titleMap = titleCache.get(courseId);

        groupMap.put(group.getId(), group);
        titleMap.put(group.getTitle(), group);

        bbGroupDAO.createOrUpdate(group);
    }

    public void delete(Long groupId, Id courseId) throws PersistenceException, ExecutionException {
        delete(getIdFromLong(groupId), courseId);
    }

    public synchronized void delete(Id groupId, Id courseId) throws PersistenceException, ExecutionException {
        Group group = groupCache.get(courseId).get(groupId);

        Map<Id, Group> groupMap = groupCache.get(courseId);
        Map<String, Group> titleMap = titleCache.get(courseId);

        groupMap.remove(groupId);
        titleMap.remove(group.getTitle());

        bbGroupDAO.delete(groupId);
    }

    public Id getIdFromLong(Long id) {
        return Id.toId(Group.DATA_TYPE, id);
    }

    public synchronized void clearCaches() {
        titleCache.invalidateAll();
        titleCache.cleanUp();

        groupCache.invalidateAll();
        titleCache.cleanUp();
    }

    public BbGroupDAO getBbGroupDAO() {
        return bbGroupDAO;
    }

    public void setBbGroupDAO(BbGroupDAO bbGroupDAO) {
        this.bbGroupDAO = bbGroupDAO;
    }
}
