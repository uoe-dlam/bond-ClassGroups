package au.edu.bond.classgroups.service;

import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;

/**
 * Created by shane on 20/01/15.
 */
public interface BbUserService {

    public User getById(Id id) throws PersistenceException;

    public User getByExternalSystemId(String externalSystemId) throws PersistenceException;

    public User getByExternalSystemId(String externalSystemId, Id courseId) throws PersistenceException;

}
