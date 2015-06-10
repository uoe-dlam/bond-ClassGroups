package au.edu.bond.classgroups.config;

/**
 * Created by Shane Argo on 13/06/2014.
 */
public class PullUrlCsvFeedDeserialiserConfig {

    private String groupsUrl;
    private String membersUrl;

    public String getGroupsUrl() {
        return groupsUrl;
    }

    public void setGroupsUrl(String groupsUrl) {
        this.groupsUrl = groupsUrl;
    }

    public String getMembersUrl() {
        return membersUrl;
    }

    public void setMembersUrl(String membersUrl) {
        this.membersUrl = membersUrl;
    }
}
