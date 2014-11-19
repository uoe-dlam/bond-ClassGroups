package au.edu.bond.classgroups.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Shane Argo on 30/05/2014.
 */
public class Group {

    private String groupId;
    private String courseId;
    private String title;
    private Collection<String> tools;
    private String leaderId;
    private String groupSet;
    private Boolean available;

    public Collection<Member> members;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<Member> getMembers() {
        return members;
    }

    public void setMembers(Collection<Member> members) {
        this.members = members;
    }

    public Collection<String> getTools() {
        return tools;
    }

    public void setTools(Collection<String> tools) {
        this.tools = tools;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
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
}
