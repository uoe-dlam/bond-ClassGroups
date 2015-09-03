package au.edu.bond.classgroups.groupext;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class GroupExtensionService {

    @Autowired
    private GroupExtensionDAO groupExtensionDAO;

    LoadingCache</*Course Id*/Long, ConcurrentMap</*External Id*/String, GroupExtension>> byIdCache;

    public GroupExtensionService(String byIdCacheSpec) {
        byIdCache = CacheBuilder.from(byIdCacheSpec).build(new CacheLoader<Long, ConcurrentMap<String, GroupExtension>>() {
            @Override
            public ConcurrentMap<String, GroupExtension> load(Long courseId) throws Exception {
                ConcurrentHashMap<String, GroupExtension> idMap = new ConcurrentHashMap<String, GroupExtension>();
                Collection<GroupExtension> groupExts = groupExtensionDAO.getByCourseId(courseId);
                for (GroupExtension ext : groupExts) {
                    idMap.put(ext.getExternalSystemId(), ext);
                }
                return idMap;
            }
        });
    }

    public GroupExtension getGroupExtensionByExternalId(String externalId, long courseId) throws ExecutionException {
        return byIdCache.get(courseId).get(externalId);
    }

    public void create(GroupExtension groupExtension, long courseId) throws ExecutionException {
        groupExtensionDAO.create(groupExtension);

        ConcurrentMap<String, GroupExtension> idMap = byIdCache.get(courseId);
        idMap.putIfAbsent(groupExtension.getExternalSystemId(), groupExtension);
    }

    public void update(GroupExtension groupExtension, long courseId) throws ExecutionException {
        groupExtensionDAO.update(groupExtension);

        ConcurrentMap<String, GroupExtension> idMap = byIdCache.get(courseId);
        idMap.put(groupExtension.getExternalSystemId(), groupExtension);
    }

    public void delete(GroupExtension groupExtension, long courseId) throws ExecutionException {
        groupExtensionDAO.delete(groupExtension);

        ConcurrentMap<String, GroupExtension> idMap = byIdCache.get(courseId);
        idMap.remove(groupExtension.getExternalSystemId());
    }
}
