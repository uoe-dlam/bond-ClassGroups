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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbGroupService implements Cleanable {

    @Autowired
    private BbGroupDAO bbGroupDAO;

    private final LoadingCache</*Course Id*/Id, ConcurrentMap</*Group Title*/String, Group>> byTitleCache;
    private final LoadingCache</*Course Id*/Id, ConcurrentMap</*Group Id*/Id, Group>> byIdCache;
    private final LoadingCache</*Group Id*/Id, /*Course Id*/Id> courseIdCache;

    public BbGroupService(final String byIdCacheSpec, final String byTitleCacheSpec, final String courseIdCacheSpec) {
        byIdCache = CacheBuilder.from(byIdCacheSpec).build(new CacheLoader<Id, ConcurrentMap<Id, Group>>() {
            @Override
            public ConcurrentMap<Id, Group> load(final Id courseId) throws Exception {
                Collection<Group> classGroups = bbGroupDAO.getByCourseId(courseId);
                ConcurrentHashMap<Id, Group> groupMap = new ConcurrentHashMap<>();

                for (Group group : classGroups) {
                    groupMap.put(group.getId(), group);
                }

                return groupMap;
            }
        });

        byTitleCache = CacheBuilder.from(byTitleCacheSpec).build(new CacheLoader<Id, ConcurrentMap<String, Group>>() {
            @Override
            public ConcurrentMap<String, Group> load(final Id courseId) throws Exception {
                Map<Id, Group> groupMap = byIdCache.get(courseId);
                ConcurrentHashMap<String, Group> titleMap = new ConcurrentHashMap<>();

                for (Group group : groupMap.values()) {
                    if (group != null && group.getTitle() != null && !group.getTitle().isEmpty()) {
                        titleMap.put(group.getTitle(), group);
                    }
                }

                return titleMap;
            }
        });

        courseIdCache = CacheBuilder.from(courseIdCacheSpec).build(new CacheLoader<Id, Id>() {
            @Override
            public Id load(final Id groupId) throws Exception {
                return bbGroupDAO.getById(groupId).getCourseId();
            }
        });
    }

    public Group getById(final long id, final Id courseId) throws ExecutionException {
        return getById(getIdFromLong(id), courseId);
    }

    private Group getById(final Id groupId, final Id courseId) throws ExecutionException {
        return byIdCache.get(courseId).get(groupId);
    }

    public Group getByTitleAndCourseId(final String title, final Id courseId) throws ExecutionException {
        if (title == null || title.isEmpty()) {
            return null;
        }

        return byTitleCache.get(courseId).get(title);
    }

    public synchronized void createOrUpdate(final Group group) throws ExecutionException, PersistenceException, ValidationException {
        bbGroupDAO.createOrUpdate(group);

        Id courseId = group.getCourseId();
        Map<Id, Group> groupMap = byIdCache.get(courseId);
        Map<String, Group> titleMap = byTitleCache.get(courseId);

        groupMap.put(group.getId(), group);
        titleMap.put(group.getTitle(), group);
    }

    public synchronized void createOrUpdate(final Group group, final Set<Id> courseMembershipIds) throws ExecutionException, PersistenceException, ValidationException {
        bbGroupDAO.createOrUpdate(group, courseMembershipIds);

        Id courseId = group.getCourseId();
        Map<Id, Group> groupMap = byIdCache.get(courseId);
        Map<String, Group> titleMap = byTitleCache.get(courseId);

        groupMap.put(group.getId(), group);
        titleMap.put(group.getTitle(), group);
    }

    private synchronized void delete(final Id groupId, final Id courseId) throws PersistenceException, ExecutionException {
        Group group = byIdCache.get(courseId).get(groupId);

        Map<Id, Group> groupMap = byIdCache.get(courseId);
        Map<String, Group> titleMap = byTitleCache.get(courseId);

        groupMap.remove(groupId);
        titleMap.remove(group.getTitle());

        bbGroupDAO.delete(groupId);
    }

    public Id getIdFromLong(final Long id) {
        return Id.toId(Group.DATA_TYPE, id);
    }

    public synchronized void clearCaches() {
        byTitleCache.invalidateAll();
        byTitleCache.cleanUp();
        byIdCache.invalidateAll();
        byIdCache.cleanUp();
    }
}
