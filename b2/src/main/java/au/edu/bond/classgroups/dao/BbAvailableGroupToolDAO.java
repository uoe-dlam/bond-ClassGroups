package au.edu.bond.classgroups.dao;

import blackboard.data.course.AvailableGroupTool;
import blackboard.data.navigation.NavigationApplication;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.GroupDbPersister;
import blackboard.persist.course.impl.AvailableGroupToolDAO;
import blackboard.persist.navigation.NavigationApplicationDbLoader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shane Argo on 17/06/2014.
 */
public class BbAvailableGroupToolDAO {

    AvailableGroupToolDAO availableGroupToolDAO;
    NavigationApplicationDbLoader navigationApplicationDbLoader;

    public AvailableGroupTool getById(Id id) throws PersistenceException {
        return getAvailableGroupToolDAO().loadById(id);
    }

    public Collection<AvailableGroupTool> getByGroupId(Id groupId) throws PersistenceException {
        return getAvailableGroupToolDAO().loadByGroupId(groupId);
    }

    public Collection<AvailableGroupTool> getByCourseId(Id courseId) throws PersistenceException {
        return getAvailableGroupToolDAO().loadByCourseId(courseId);
    }

    public void createOrUpdate(AvailableGroupTool availableGroupTool) throws PersistenceException {
        getAvailableGroupToolDAO().persist(availableGroupTool);
    }

    public void delete(Id id) throws PersistenceException {
        getAvailableGroupToolDAO().deleteById(id);
    }

    public Collection<NavigationApplication> getAllGroupTools() throws PersistenceException {
        return getNavigationApplicationDbLoader().loadByTypeAndFilter(NavigationApplicationDbLoader.Type.GROUP, NavigationApplicationDbLoader.Filter.ALL);
    }

    public AvailableGroupToolDAO getAvailableGroupToolDAO() throws PersistenceException {
        if(availableGroupToolDAO == null) {
            availableGroupToolDAO = AvailableGroupToolDAO.get();
        }
        return availableGroupToolDAO;
    }

    public NavigationApplicationDbLoader getNavigationApplicationDbLoader() throws PersistenceException {
        if(navigationApplicationDbLoader == null) {
            navigationApplicationDbLoader = NavigationApplicationDbLoader.Default.getInstance();
        }
        return navigationApplicationDbLoader;
    }

}
