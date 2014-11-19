package au.edu.bond.classgroups.config.model;

/**
 * Created by Shane Argo on 13/06/2014.
 */
public class PullFileCsvFeedDeserialiserConfig {

    private String groupsFilePath;
    private String membersFilePath;

    public String getGroupsFilePath() {
        return groupsFilePath;
    }

    public void setGroupsFilePath(String groupsFilePath) {
        this.groupsFilePath = groupsFilePath;
    }

    public String getMembersFilePath() {
        return membersFilePath;
    }

    public void setMembersFilePath(String membersFilePath) {
        this.membersFilePath = membersFilePath;
    }
}
