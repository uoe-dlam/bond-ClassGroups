package au.edu.bond.classgroups.groupext;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Shane Argo on 5/06/2014.
 */
@Entity
@Table(name="bond_ClassGroups_groupext")
public class GroupExtension {

    private String externalSystemId;
    private Long internalGroupId;
    private String title;
    private boolean synced = true;
    private Long customViewId;
    private Long leaderFeedCourseUserId;
    private Long leaderOverrideCourseUserId;
    private boolean leaderOverridden = false;

    @Id
    @Column(name="external_system_id")
    public String getExternalSystemId() {
        return externalSystemId;
    }

    public void setExternalSystemId(String externalSystemId) {
        this.externalSystemId = externalSystemId;
    }

    @Column(name="groups_pk1")
    public Long getInternalGroupId() {
        return internalGroupId;
    }

    public void setInternalGroupId(Long internalGroupId) {
        this.internalGroupId = internalGroupId;
    }

    @Column(name="title")
    public void setTitle(String baseTitle) {
        this.title = baseTitle;
    }

    public String getTitle() {
        return title;
    }

    @Column(name="sync_ind")
    @Type(type="yes_no")
    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    @Column(name="gradebook_custom_view_pk1")
    public Long getCustomViewId() {
        return customViewId;
    }

    public void setCustomViewId(Long customViewId) {
        this.customViewId = customViewId;
    }

    @Column(name="leader_feed_cu_pk1")
    public Long getLeaderFeedCourseUserId() {
        return leaderFeedCourseUserId;
    }

    public void setLeaderFeedCourseUserId(Long leaderFeedCourseUserId) {
        this.leaderFeedCourseUserId = leaderFeedCourseUserId;
    }

    @Column(name="leader_override_cu_pk1")
    public Long getLeaderOverrideCourseUserId() {
        return leaderOverrideCourseUserId;
    }

    public void setLeaderOverrideCourseUserId(Long leaderOverrideCourseUserId) {
        this.leaderOverrideCourseUserId = leaderOverrideCourseUserId;
    }

    @Column(name="leader_override_ind")
    @Type(type="yes_no")
    public boolean isLeaderOverridden() {
        return leaderOverridden;
    }

    public void setLeaderOverridden(boolean leaderOverridden) {
        this.leaderOverridden = leaderOverridden;
    }

    public Long calculateLeaderCourseUserId() {
        return leaderOverridden ? leaderOverrideCourseUserId : leaderFeedCourseUserId;
    }
}
