package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.dao.BbCourseDAO;
import blackboard.data.course.Course;
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

    public Course getByExternalSystemId(String externalSystemId) throws PersistenceException {
        Course course = cache.get(externalSystemId);
        if(course == null) {
            course = bbCourseDAO.getByExternalSystemId(externalSystemId);
            cache.put(externalSystemId, course);
        }
        return course;
    }

}
