package au.edu.bond.classgroups.dao;

import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.persist.user.UserDbLoader;

import java.util.Collection;

/**
 * Created by Shane Argo on 10/06/2014.
 */
public class BbCourseMembershipDAO {

    CourseMembershipDbLoader courseMembershipDbLoader;


    public CourseMembership getById(Id id) throws PersistenceException {
        return getCourseMembershipDbLoader().loadById(id);
    }

    public CourseMembership getById(long id) throws PersistenceException {
        Id bbId = Id.toId(CourseMembership.DATA_TYPE, id);
        return getById(bbId);
    }

    public Collection<CourseMembership> getByCourseId(Id id) throws PersistenceException {
        return getCourseMembershipDbLoader().loadByCourseId(id);
    }

    public Collection<CourseMembership> getByCourseId(long id) throws PersistenceException {
        Id bbId = Id.toId(Course.DATA_TYPE, id);
        return getByCourseId(id);
    }

    public CourseMembership getByCourseIdAndUserId(Id courseId, Id userId) throws PersistenceException {
        return getCourseMembershipDbLoader().loadByCourseAndUserId(courseId, userId);
    }

    public CourseMembershipDbLoader getCourseMembershipDbLoader() throws PersistenceException {
        if(courseMembershipDbLoader == null) {
            courseMembershipDbLoader = CourseMembershipDbLoader.Default.getInstance();
        }
        return courseMembershipDbLoader;
    }

}
