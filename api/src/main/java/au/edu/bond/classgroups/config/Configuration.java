package au.edu.bond.classgroups.config;

import au.edu.bond.classgroups.model.Schedule;

import java.util.List;

/**
 * Created by Shane Argo on 12/06/2014.
 */
public class Configuration {

    public enum ToolsMode {
        CREATE, READD, SYNC
    }

    public enum LeaderChangedMode {
        OVERRIDE, FEED
    }

    public enum AvailabilityMode {
        CREATE, UPDATE
    }

    public enum GroupAvailability {
        AVAILABLE, UNAVAILABLE
    }

    public enum FeedFetcherType {
        URL, FILE
    }

    public enum LoggingLevel {
        NORMAL, DEBUG
    }

    private FeedFetcherType feedFetcherType;
    private ToolsMode toolsMode;
    private List<String> defaultTools;
    private LeaderChangedMode leaderChangedMode;
    private AvailabilityMode availabilityMode;
    private GroupAvailability defaultAvailability;
    private PullFileCsvFeedDeserialiserConfig pullFileCsvFeedDeserialiser;
    private PullUrlCsvFeedDeserialiserConfig pullUrlCsvFeedDeserialiser;
    private PushCsvFeedDeserialiserConfig pushCsvFeedDeserialiser;
    private boolean autoCleanUpOldTasks;
    private int cleanUpDaysToKeep;
    private boolean schedulesEnabled;
    private List<Schedule> schedules;
    private boolean enrolLeaderIfMissing;
    private int processingThreads;
    private String processingServerNamePattern;
    private int queuePollingFrequencySeconds;
    private FeedHeaderConfig feedHeaderConfig;
    private GroupAvailability groupSetAvailability;
    private LoggingLevel loggingLevel;


    public ToolsMode getToolsMode() {
        return toolsMode;
    }

    public void setToolsMode(ToolsMode toolsMode) {
        this.toolsMode = toolsMode;
    }

    public List<String> getDefaultTools() {
        return defaultTools;
    }

    public void setDefaultTools(List<String> defaultTools) {
        this.defaultTools = defaultTools;
    }

    public LeaderChangedMode getLeaderChangedMode() {
        return leaderChangedMode;
    }

    public void setLeaderChangedMode(LeaderChangedMode leaderChangedMode) {
        this.leaderChangedMode = leaderChangedMode;
    }

    public AvailabilityMode getAvailabilityMode() {
        return availabilityMode;
    }

    public void setAvailabilityMode(AvailabilityMode availabilityMode) {
        this.availabilityMode = availabilityMode;
    }

    public GroupAvailability getDefaultAvailability() {
        return defaultAvailability;
    }

    public void setDefaultAvailability(GroupAvailability defaultAvailability) {
        this.defaultAvailability = defaultAvailability;
    }

    public PullFileCsvFeedDeserialiserConfig getPullFileCsvFeedDeserialiser() {
        return pullFileCsvFeedDeserialiser;
    }

    public void setPullFileCsvFeedDeserialiser(PullFileCsvFeedDeserialiserConfig pullFileCsvFeedDeserialiser) {
        this.pullFileCsvFeedDeserialiser = pullFileCsvFeedDeserialiser;
    }

    public PullUrlCsvFeedDeserialiserConfig getPullUrlCsvFeedDeserialiser() {
        return pullUrlCsvFeedDeserialiser;
    }

    public void setPullUrlCsvFeedDeserialiser(PullUrlCsvFeedDeserialiserConfig pullUrlCsvFeedDeserialiser) {
        this.pullUrlCsvFeedDeserialiser = pullUrlCsvFeedDeserialiser;
    }

    public PushCsvFeedDeserialiserConfig getPushCsvFeedDeserialiser() {
        return pushCsvFeedDeserialiser;
    }

    public void setPushCsvFeedDeserialiser(PushCsvFeedDeserialiserConfig pushCsvFeedDeserialiser) {
        this.pushCsvFeedDeserialiser = pushCsvFeedDeserialiser;
    }

    public boolean isAutoCleanUpOldTasks() {
        return autoCleanUpOldTasks;
    }

    public void setAutoCleanUpOldTasks(boolean autoCleanUpOldTasks) {
        this.autoCleanUpOldTasks = autoCleanUpOldTasks;
    }

    public int getCleanUpDaysToKeep() {
        return cleanUpDaysToKeep;
    }

    public void setCleanUpDaysToKeep(int cleanUpDaysToKeep) {
        this.cleanUpDaysToKeep = cleanUpDaysToKeep;
    }

    public boolean isSchedulesEnabled() {
        return schedulesEnabled;
    }

    public void setSchedulesEnabled(boolean schedulesEnabled) {
        this.schedulesEnabled = schedulesEnabled;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public boolean isEnrolLeaderIfMissing() {
        return enrolLeaderIfMissing;
    }

    public void setEnrolLeaderIfMissing(boolean enrolLeaderIfMissing) {
        this.enrolLeaderIfMissing = enrolLeaderIfMissing;
    }

    public int getProcessingThreads() {
        return processingThreads;
    }

    public void setProcessingThreads(int processingThreads) {
        this.processingThreads = processingThreads;
    }

    public FeedHeaderConfig getFeedHeaderConfig() {
        return feedHeaderConfig;
    }

    public void setFeedHeaderConfig(FeedHeaderConfig feedHeaderConfig) {
        this.feedHeaderConfig = feedHeaderConfig;
    }

    public GroupAvailability getGroupSetAvailability() {
        return groupSetAvailability;
    }

    public void setGroupSetAvailability(GroupAvailability groupSetAvailability) {
        this.groupSetAvailability = groupSetAvailability;
    }

    public FeedFetcherType getFeedFetcherType() {
        return feedFetcherType;
    }

    public void setFeedFetcherType(FeedFetcherType feedFetcherType) {
        this.feedFetcherType = feedFetcherType;
    }

    public String getProcessingServerNamePattern() {
        return processingServerNamePattern;
    }

    public void setProcessingServerNamePattern(String processingServerNamePattern) {
        this.processingServerNamePattern = processingServerNamePattern;
    }

    public int getQueuePollingFrequencySeconds() {
        return queuePollingFrequencySeconds;
    }

    public void setQueuePollingFrequencySeconds(int queuePollingFrequencySeconds) {
        this.queuePollingFrequencySeconds = queuePollingFrequencySeconds;
    }

    public LoggingLevel getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(LoggingLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

}
