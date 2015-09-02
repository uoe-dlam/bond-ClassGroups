package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbGradebookCustomViewDAO;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.platform.gradebook2.GradebookCustomView;
import blackboard.platform.security.authentication.BbSecurityException;
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
 * Created by Shane Argo on 19/06/2014.
 */
public class BbGradebookCustomViewService {

    @Autowired
    private BbGradebookCustomViewDAO bbGradebookCustomViewDAO;

    private LoadingCache</*Course Id*/Id, ConcurrentMap</*Custom View Id*/Id, GradebookCustomView>> byIdCache;

    public BbGradebookCustomViewService(int cacheSize) {
        byIdCache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(new CacheLoader<Id, ConcurrentMap<Id, GradebookCustomView>>() {
            @Override
            public ConcurrentMap<Id, GradebookCustomView> load(Id courseId) throws Exception {
                final ConcurrentHashMap<Id, GradebookCustomView> viewMap = new ConcurrentHashMap<Id, GradebookCustomView>();
                final Collection<GradebookCustomView> views = bbGradebookCustomViewDAO.getByCourseId(courseId);
                for (GradebookCustomView view : views) {
                    viewMap.put(view.getId(), view);
                }
                return viewMap;
            }
        });
    }

    public GradebookCustomView getById(Id customViewId, Id courseId) throws ExecutionException {
        return byIdCache.get(courseId).get(customViewId);
    }

    public synchronized void createOrUpdate(GradebookCustomView gradebookCustomView) throws BbSecurityException, ExecutionException {
        bbGradebookCustomViewDAO.createOrUpdate(gradebookCustomView);

        final ConcurrentMap<Id, GradebookCustomView> viewMap = byIdCache.get(gradebookCustomView.getCourseId());
        viewMap.put(gradebookCustomView.getId(), gradebookCustomView);
    }

    public Id getIdFromLong(long customViewId) {
        return Id.toId(GradebookCustomView.DATA_TYPE, customViewId);
    }

    public BbGradebookCustomViewDAO getBbGradebookCustomViewDAO() {
        return bbGradebookCustomViewDAO;
    }

    public void setBbGradebookCustomViewDAO(BbGradebookCustomViewDAO bbGradebookCustomViewDAO) {
        this.bbGradebookCustomViewDAO = bbGradebookCustomViewDAO;
    }
}
