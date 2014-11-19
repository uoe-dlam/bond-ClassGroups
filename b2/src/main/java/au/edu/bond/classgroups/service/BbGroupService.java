package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbGroupDAO;
import blackboard.data.ValidationException;
import blackboard.data.course.Group;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbGroupService {

    @Autowired
    private BbGroupDAO bbGroupDAO;

    private Map<Id, Group> idCache = new HashMap<Id, Group>();
    private Map<Id, Map<String, Group>> titleCache = new HashMap<Id, Map<String, Group>>();

    public Group getById(long id) throws PersistenceException {
        return getById(getIdFromLong(id));
    }

    public Group getById(long id, Id courseId) throws PersistenceException {
        return getById(getIdFromLong(id), courseId);
    }

    public Group getById(Id id) throws PersistenceException {
        Group group = idCache.get(id);
        if(group == null) {
            group = bbGroupDAO.getById(id);
            cache(group);
        }
        return group;
    }

    public Group getById(Id groupId, Id courseId) throws PersistenceException {
        Group group = idCache.get(groupId);
        if(group == null) {
            Collection<Group> classGroups = bbGroupDAO.getByCourseId(courseId);
            for(Group classGroup : classGroups) {
                cache(classGroup);
                if(classGroup.getId().equals(groupId)){
                    group = classGroup;
                }
            }
        }
        return group;
    }

    public Collection<Group> getByCourseId(Id courseId) throws PersistenceException {
        Map<String, Group> courseTitleCache = titleCache.get(courseId);
        if(courseTitleCache != null) {
            return courseTitleCache.values();
        }

        Collection<Group> classGroups = bbGroupDAO.getByCourseId(courseId);
        for(Group classGroup : classGroups) {
            cache(classGroup);
        }
        return classGroups;
    }

    public Group getByTitleAndCourseId(String title, Id courseId) throws PersistenceException {
        Group group = null;
        Map<String, Group> courseTitleCache = titleCache.get(courseId);
        if(courseTitleCache != null) {
            group = courseTitleCache.get(title);
        }
        if(group != null) {
            return group;
        }

        Collection<Group> classGroups = bbGroupDAO.getByCourseId(courseId);
        for(Group classGroup : classGroups) {
            cache(classGroup);
            if(classGroup.getTitle().equals(title)){
                group = classGroup;
            }
        }
        return group;
    }

    public void createOrUpdate(Group group) throws ValidationException, PersistenceException {
        bbGroupDAO.createOrUpdate(group);
        cache(group);
    }

    public void delete(Long id) throws PersistenceException {
        delete(getIdFromLong(id));
    }

    public void delete(Id id) throws PersistenceException {
        bbGroupDAO.delete(id);
        Group group = idCache.get(id);
        if(group != null) {
            uncache(group);
        }
    }

    public Id getIdFromLong(Long id) {
        return Id.toId(Group.DATA_TYPE, id);
    }

    private void cache(Group group) {
        idCache.put(group.getId(), group);

        Map<String, Group> courseTitleCache = titleCache.get(group.getCourseId());
        if(courseTitleCache == null) {
            courseTitleCache = new HashMap<String, Group>();
            titleCache.put(group.getCourseId(), courseTitleCache);
        }
        courseTitleCache.put(group.getTitle(), group);
    }

    private void uncache(Group group) {
        idCache.remove(group.getId());

        Map<String, Group> courseTitleCache = titleCache.get(group.getCourseId());
        if(courseTitleCache != null) {
            courseTitleCache.remove(group.getTitle());
        }
    }

    public BbGroupDAO getBbGroupDAO() {
        return bbGroupDAO;
    }

    public void setBbGroupDAO(BbGroupDAO bbGroupDAO) {
        this.bbGroupDAO = bbGroupDAO;
    }
}
