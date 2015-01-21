package au.edu.bond.classgroups.service;

import blackboard.data.course.CourseMembership;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;

/**
 * Created by shane on 20/01/15.
 */
public interface BbCourseMembershipService {

    public CourseMembership getById(long id) throws PersistenceException;

    public CourseMembership getById(Id id) throws PersistenceException;

    public CourseMembership getById(Id courseMembershipId, Id courseId) throws PersistenceException;

    public CourseMembership getByCourseIdAndUserId(Id courseId, Id userId) throws PersistenceException;

}
