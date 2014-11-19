package au.edu.bond.classgroups.manager;

import au.edu.bond.classgroups.model.Group;

import java.util.Collection;

/**
 * Created by Shane Argo on 4/06/2014.
 */
public interface GroupManager {

    public static enum Status {
        CREATED, UPDATED, UNCHANGED, ERROR, NOSYNC
    }

    public Status syncGroup(Group group);

}
