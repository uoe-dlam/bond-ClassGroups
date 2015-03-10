package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbCourseDAO;
import blackboard.data.course.Course;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class BbCourseService {

    @Autowired
    private BbCourseDAO bbCourseDAO;

    private Map<String, Course> cache = new HashMap<String, Course>();
    private Map<Id, Course> idCache = new HashMap<Id, Course>();
    private Map<Id, Id> parentIdCache = new HashMap<Id, Id>();

    public Course getById(Id id) throws PersistenceException {
        Course course = idCache.get(id);
        if(course == null) {
            course = bbCourseDAO.getById(id);
            idCache.put(id, course);
            cache.put(course.getBatchUid(), course);
        }
        return course;
    }

    public Course getByExternalSystemId(String externalSystemId) throws PersistenceException {
        Course course = cache.get(externalSystemId);
        if(course == null) {
            course = bbCourseDAO.getByExternalSystemId(externalSystemId);
            cache.put(externalSystemId, course);
            idCache.put(course.getId(), course);
        }
        return course;
    }

    public Id getParentId(Id childId) throws PersistenceException {
        Id parentId = parentIdCache.get(childId);
        if(parentId == null) {
            parentId = bbCourseDAO.getParentCourseId(childId);
            parentIdCache.put(childId, parentId);
        }
        return parentId;
    }

}
