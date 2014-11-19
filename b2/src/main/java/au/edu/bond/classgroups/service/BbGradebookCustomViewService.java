package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbGradebookCustomViewDAO;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.platform.gradebook2.GradebookCustomView;
import blackboard.platform.security.authentication.BbSecurityException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shane Argo on 19/06/2014.
 */
public class BbGradebookCustomViewService {

    @Autowired
    private BbGradebookCustomViewDAO bbGradebookCustomViewDAO;

    Map<Id, GradebookCustomView> cache = new HashMap<Id, GradebookCustomView>();

    public GradebookCustomView getById(Id id) throws KeyNotFoundException {
        GradebookCustomView gradebookCustomView = cache.get(id);
        if(gradebookCustomView == null) {
            gradebookCustomView = bbGradebookCustomViewDAO.getById(id);
            cache.put(id, gradebookCustomView);
        }
        return gradebookCustomView;
    }

    public GradebookCustomView getById(Id customViewId, Id courseId) throws KeyNotFoundException {
        GradebookCustomView gradebookCustomView = cache.get(customViewId);
        if(gradebookCustomView == null) {
            Collection<GradebookCustomView> courseGradebookCustomViews = bbGradebookCustomViewDAO.getByCourseId(courseId);
            for(GradebookCustomView courseGradebookCustomView : courseGradebookCustomViews) {
                cache.put(courseGradebookCustomView.getId(), courseGradebookCustomView);
                if(courseGradebookCustomView.getId().equals(customViewId)) {
                    gradebookCustomView = courseGradebookCustomView;
                }
            }
        }
        return gradebookCustomView;
    }

    public void createOrUpdate(GradebookCustomView gradebookCustomView) throws BbSecurityException {
        bbGradebookCustomViewDAO.createOrUpdate(gradebookCustomView);
        cache.put(gradebookCustomView.getId(), gradebookCustomView);
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
