package au.edu.bond.classgroups.dao;

import blackboard.data.ValidationException;
import blackboard.data.course.Course;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseCourseDbLoader;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.course.CourseDbPersister;
import org.springframework.stereotype.Service;

/**
 * Created by Shane on 10/06/2014.
 */
public class BbCourseDAO {

    CourseDbLoader courseDbLoader;
    CourseCourseDbLoader courseCourseDbLoader;
//    CourseDbPersister courseDbPersister;

    public Course getById(Id id) throws PersistenceException {
        return getCourseDbLoader().loadById(id);
    }

    public Course getByExternalSystemId(String externalSystemId) throws PersistenceException {
        return getCourseDbLoader().loadByBatchUid(externalSystemId);
    }

    public Id getParentCourseId(Id childId) throws PersistenceException {
        return getCourseCourseDbLoader().loadParent(childId).getParentCourseId();
    }

//    public void createOrUpdate(Course course) throws ValidationException, PersistenceException {
//        getCourseDbPersister().persist(course);
//    }
//
//    public void delete(long id) throws PersistenceException {
//        Id bbId = Id.toId(Course.DATA_TYPE, id);
//        getCourseDbPersister().deleteById(bbId);
//    }

    public CourseDbLoader getCourseDbLoader() throws PersistenceException {
        if(courseDbLoader == null) {
            courseDbLoader = CourseDbLoader.Default.getInstance();
        }
        return courseDbLoader;
    }

    public CourseCourseDbLoader getCourseCourseDbLoader() throws PersistenceException {
        if(courseCourseDbLoader == null) {
            courseCourseDbLoader = CourseCourseDbLoader.Default.getInstance();
        }
        return courseCourseDbLoader;
    }

//    public CourseDbPersister getCourseDbPersister() throws PersistenceException {
//        if(courseDbPersister == null) {
//            courseDbPersister = CourseDbPersister.Default.getInstance();
//        }
//        return courseDbPersister;
//    }

}
