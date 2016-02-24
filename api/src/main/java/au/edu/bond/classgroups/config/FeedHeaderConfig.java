package au.edu.bond.classgroups.config;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by shane on 5/09/2015.
 */
public class FeedHeaderConfig {

    private String courseIdHeader;
    private String groupIdHeader;
    private String titleHeader;
    private String leaderHeader;
    private String groupSetHeader;
    private String availableHeader;
    private String toolsHeader;
    private String userIdHeader;

    public Collection<String> getRequiredGroupsFeedHeaders() {
        final HashSet<String> headers = new HashSet<>();
        headers.add(courseIdHeader);
        headers.add(groupIdHeader);
        headers.add(titleHeader);
//        headers.add(leaderHeader);
//        headers.add(groupSetHeader);
//        headers.add(availableHeader);
//        headers.add(toolsHeader);
        return headers;
    }

    public Collection<String> getRequiredMembersFeedHeaders() {
        final HashSet<String> headers = new HashSet<>();
        headers.add(groupIdHeader);
        headers.add(userIdHeader);
        return headers;
    }

    public String getCourseIdHeader() {
        return courseIdHeader;
    }

    public void setCourseIdHeader(String courseIdHeader) {
        this.courseIdHeader = courseIdHeader;
    }

    public String getGroupIdHeader() {
        return groupIdHeader;
    }

    public void setGroupIdHeader(String groupIdHeader) {
        this.groupIdHeader = groupIdHeader;
    }

    public String getTitleHeader() {
        return titleHeader;
    }

    public void setTitleHeader(String titleHeader) {
        this.titleHeader = titleHeader;
    }

    public String getLeaderHeader() {
        return leaderHeader;
    }

    public void setLeaderHeader(String leaderHeader) {
        this.leaderHeader = leaderHeader;
    }

    public String getGroupSetHeader() {
        return groupSetHeader;
    }

    public void setGroupSetHeader(String groupSetHeader) {
        this.groupSetHeader = groupSetHeader;
    }

    public String getAvailableHeader() {
        return availableHeader;
    }

    public void setAvailableHeader(String availableHeader) {
        this.availableHeader = availableHeader;
    }

    public String getToolsHeader() {
        return toolsHeader;
    }

    public void setToolsHeader(String toolsHeader) {
        this.toolsHeader = toolsHeader;
    }

    public String getUserIdHeader() {
        return userIdHeader;
    }

    public void setUserIdHeader(String userIdHeader) {
        this.userIdHeader = userIdHeader;
    }
}
