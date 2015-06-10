package au.edu.bond.classgroups.dao;

import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.platform.gradebook2.GradebookCustomView;
import blackboard.platform.gradebook2.GradebookManager;
import blackboard.platform.gradebook2.GradebookManagerFactory;
import blackboard.platform.gradebook2.impl.GradebookCustomViewDAO;
import blackboard.platform.security.authentication.BbSecurityException;

import java.util.Collection;

/**
 * Created by Shane Argo on 19/06/2014.
 */
public class BbGradebookCustomViewDAO {

    private GradebookCustomViewDAO gradebookCustomViewDAO;

    public GradebookCustomView getById(long id) throws KeyNotFoundException {
        Id bbId = Id.toId(GradebookCustomView.DATA_TYPE, id);
        return getById(bbId);
    }

    public GradebookCustomView getById(Id id) throws KeyNotFoundException {
        return getGradebookCustomViewDAO().loadById(id);
    }

    public Collection<GradebookCustomView> getByCourseId(Id courseId) {
        return getGradebookCustomViewDAO().getCustomViews(courseId, 0L);
    }

    public void createOrUpdate(GradebookCustomView gradebookCustomView) throws BbSecurityException {
        getGradebookCustomViewDAO().persist(gradebookCustomView);
    }

//    public void delete(Id id) {
//        getGradebookCustomViewDAO().deleteById(id);
//    }
//
//    public void delete(long id) {
//        Id bbId = Id.toId(GradebookCustomView.DATA_TYPE, id);
//        delete(bbId);
//    }
//
//    public void delete(GradebookCustomView gradebookCustomView) {
//        delete(gradebookCustomView.getId());
//    }

    public GradebookCustomViewDAO getGradebookCustomViewDAO() {
        if(gradebookCustomViewDAO == null) {
            gradebookCustomViewDAO = GradebookCustomViewDAO.get();
        }
        return gradebookCustomViewDAO;
    }

}
