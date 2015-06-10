package au.edu.bond.classgroups.feed.csv;

import java.util.Collection;

/**
 * Created by Shane Argo on 2/06/2014.
 */
public class GroupCsvRow {

    String courseId;
    String groupId;
    String title;
    String leader;
    String groupSet;
    Boolean available;
    Collection<String> tools;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getGroupSet() {
        return groupSet;
    }

    public void setGroupSet(String groupSet) {
        this.groupSet = groupSet;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Collection<String> getTools() {
        return tools;
    }

    public void setTools(Collection<String> tools) {
        this.tools = tools;
    }

}
