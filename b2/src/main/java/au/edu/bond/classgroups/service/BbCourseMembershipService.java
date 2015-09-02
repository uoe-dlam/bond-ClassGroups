package au.edu.bond.classgroups.service;

import blackboard.data.ValidationException;
import blackboard.data.course.CourseMembership;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;

import java.util.concurrent.ExecutionException;

/**
 * Created by shane on 20/01/15.
 */
public interface BbCourseMembershipService {

    CourseMembership getById(long id, Id courseId) throws PersistenceException, ExecutionException;

    CourseMembership getById(Id id, Id courseId) throws PersistenceException, ExecutionException;

    CourseMembership getByCourseIdAndUserId(Id courseId, Id userId) throws PersistenceException, ExecutionException;

    void persistCourseMembership(CourseMembership courseMembership) throws ValidationException, PersistenceException, ExecutionException;

}
