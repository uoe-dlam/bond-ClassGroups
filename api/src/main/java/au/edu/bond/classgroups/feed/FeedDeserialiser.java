package au.edu.bond.classgroups.feed;

import au.edu.bond.classgroups.exception.FeedDeserialisationException;
import au.edu.bond.classgroups.model.Group;

import java.util.Collection;

/**
 * Responsible for translating incoming data feeds into a standard POJOs.
 * Created by Shane Argo on 2/06/2014.
 */
public interface FeedDeserialiser {

    public Collection<Group> getGroups() throws FeedDeserialisationException;

}
