package au.edu.bond.classgroups.feed.csv;

import au.edu.bond.classgroups.exception.FeedDeserialisationException;
import au.edu.bond.classgroups.feed.FeedDeserialiser;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.model.Group;
import au.edu.bond.classgroups.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Collection;

/**
 * Created by Shane Argo on 13/06/2014.
 */
public class FileCsvFeedDeserialiser implements FeedDeserialiser {

    @Autowired
    private TaskLogger currentTaskLogger;
    @Autowired
    private CsvFeedDeserialiser csvFeedDeserialiser;
    @Autowired
    private ResourceService resourceService;

    private File groupsFile;
    private File membersFile;

    @Override
    public Collection<Group> getGroups() throws FeedDeserialisationException {
        InputStream groupsIS = null;
        InputStream membersIS = null;
        try {
            try {
                groupsIS = new FileInputStream(groupsFile);
            } catch (FileNotFoundException e) {
                currentTaskLogger.error("bond.classgroups.error.opengroupsfailed");
                throw new FeedDeserialisationException(
                        resourceService.getLocalisationString("bond.classgroups.exception.opengroupsfailed"), e);
            }

            try {
                membersIS = new FileInputStream(membersFile);
            } catch (FileNotFoundException e) {
                currentTaskLogger.error("bond.classgroups.error.openmembersfailed");
                throw new FeedDeserialisationException(
                        resourceService.getLocalisationString("bond.classgroups.exception.openmembersfailed"), e);
            }

            csvFeedDeserialiser.setGroupsInputStream(groupsIS);
            csvFeedDeserialiser.setMembersInputStream(membersIS);

            return csvFeedDeserialiser.getGroups();
        } finally {
            if(groupsIS != null) {
                try {
                    groupsIS.close();
                } catch (IOException e) {
                    currentTaskLogger.error("bond.classgroups.error.failedclosegroups");
                }
            }
            if(membersIS != null) {
                try {
                    membersIS.close();
                } catch (IOException e) {
                    currentTaskLogger.error("bond.classgroups.error.failedclosemembers");
                }
            }
        }
    }

    public TaskLogger getCurrentTaskLogger() {
        return currentTaskLogger;
    }

    public void setCurrentTaskLogger(TaskLogger currentTaskLogger) {
        this.currentTaskLogger = currentTaskLogger;
    }

    public File getGroupsFile() {
        return groupsFile;
    }

    public void setGroupsFile(File groupsFile) {
        this.groupsFile = groupsFile;
    }

    public File getMembersFile() {
        return membersFile;
    }

    public void setMembersFile(File membersFile) {
        this.membersFile = membersFile;
    }


}
