package au.edu.bond.classgroups.feed.csv;

import au.edu.bond.classgroups.exception.FeedDeserialisationException;
import au.edu.bond.classgroups.feed.FeedDeserialiser;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.model.Group;
import au.edu.bond.classgroups.model.Member;
import au.edu.bond.classgroups.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Reads CSV data from an input stream and translates it into groups and members
 * Created by Shane Argo on 2/06/2014.
 */
public class CsvFeedDeserialiser implements FeedDeserialiser {

    @Autowired
    private TaskLogger currentTaskLogger;
    @Autowired
    private ResourceService resourceService;

    private CsvPreference csvPreference = CsvPreference.STANDARD_PREFERENCE;

    private InputStream groupsInputStream;
    private InputStream membersInputStream;

    @Override
    public Collection<Group> getGroups() throws FeedDeserialisationException {
        //Map instead of list/collection so that the groups can be easily found again during member processing.
        Map<String, Group> groups = new HashMap<String, Group>();

        CsvBeanReader groupsBeanReader = null;
        CsvBeanReader membersBeanReader = null;

        try {
            groupsBeanReader = new CsvBeanReader(new InputStreamReader(groupsInputStream), csvPreference);
            membersBeanReader = new CsvBeanReader(new InputStreamReader(membersInputStream), csvPreference);

            boolean stop = false;
            final String[] groupsColumnHeaders = getCsvHeaders(groupsBeanReader);
            if (groupsColumnHeaders == null || groupsColumnHeaders.length == 0) {
                currentTaskLogger.error("bond.classgroups.error.missingheadergroups");
                stop = true;
            }

            final String[] membersColumnHeaders = getCsvHeaders(membersBeanReader);
            if (membersColumnHeaders == null || membersColumnHeaders.length == 0) {
                currentTaskLogger.error("bond.classgroups.error.missingheadermembers");
                stop = true;
            }

            if (stop) {
                return null;
            }

            final CellProcessor[] groupsCellProcessors = CellProcessorUtil.getCellProcessorsForGroupColumns(groupsColumnHeaders);
            final CellProcessor[] membersCellProcessors = CellProcessorUtil.getCellProcessorsForMemberColumns(membersColumnHeaders);

            GroupCsvRow groupCsvRow;
            while ((groupCsvRow = groupsBeanReader.read(GroupCsvRow.class, groupsColumnHeaders, groupsCellProcessors)) != null) {
                groups.put(groupCsvRow.getGroupId(), translateRowToGroup(groupCsvRow));

                if(Thread.currentThread().isInterrupted()) {
                    currentTaskLogger.error("bond.classgroups.error.groupsthreadinterrupted");
                    throw new FeedDeserialisationException(resourceService.getLocalisationString(
                            "bond.classgroups.exception.groupsthreadinterrupted"));
                }
            }

            MemberCsvRow memberCsvRow;
            while ((memberCsvRow = membersBeanReader.read(MemberCsvRow.class, membersColumnHeaders, membersCellProcessors)) != null) {
                Group group = groups.get(memberCsvRow.getGroupId());

                if (group == null) {
                    currentTaskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.memberformissinggroup",
                            membersBeanReader.getLineNumber(), memberCsvRow.getGroupId()));
                    continue;
                }

                Collection<Member> members = group.getMembers();
                if (members == null) {
                    members = new HashSet<Member>();
                    group.setMembers(members);
                }

                members.add(translateRowToMember(memberCsvRow));

                if(Thread.currentThread().isInterrupted()) {
                    currentTaskLogger.error("bond.classgroups.error.membersthreadinterrupted");
                    throw new FeedDeserialisationException(resourceService.getLocalisationString(
                            "bond.classgroups.exception.membersthreadinterrupted"));
                }
            }
        } catch (IOException e) {
            currentTaskLogger.error("bond.classgroups.error.failedtoparsecsv", e);
            throw new FeedDeserialisationException(resourceService.getLocalisationString(
                    "bond.classgroups.exception.failedtoparsecsv"), e);
        } finally {
            if(groupsBeanReader != null) {
                try {
                    groupsBeanReader.close();
                } catch (IOException e) {
                    currentTaskLogger.error("bond.classgroups.error.failedclosegroupsbeanreader");
                }
            }
            if(membersBeanReader != null) {
                try {
                    membersBeanReader.close();
                } catch (IOException e) {
                    currentTaskLogger.error("bond.classgroups.error.failedclosemembersbeanreader");
                }
            }
        }

        return groups.values();
    }


    private String[] getCsvHeaders(CsvBeanReader beanReader) {
        try {
            return beanReader.getHeader(/* First row check*/ true);
        } catch (IOException e) {
            currentTaskLogger.error("bond.classgroups.error.failedtoreadcsvheaders", e);
            return null;
        }
    }

    private Group translateRowToGroup(GroupCsvRow row) {
        Group group = new Group();

        group.setCourseId(row.getCourseId());
        group.setGroupId(row.getGroupId());
        group.setTitle(row.getTitle());
        group.setLeaderId(row.getLeader());
        group.setGroupSet(row.getGroupSet());
        group.setAvailable(row.getAvailable());
        group.setTools(row.getTools());

        return group;
    }

    private Member translateRowToMember(MemberCsvRow row) {
        Member member = new Member();

        member.setUserId(row.getUserId());

        return member;
    }

    public InputStream getGroupsInputStream() {
        return groupsInputStream;
    }

    public void setGroupsInputStream(InputStream groupsInputStream) {
        this.groupsInputStream = groupsInputStream;
    }

    public InputStream getMembersInputStream() {
        return membersInputStream;
    }

    public void setMembersInputStream(InputStream membersInputStream) {
        this.membersInputStream = membersInputStream;
    }

    public TaskLogger getCurrentTaskLogger() {
        return currentTaskLogger;
    }

    public void setCurrentTaskLogger(TaskLogger currentTaskLogger) {
        this.currentTaskLogger = currentTaskLogger;
    }

    public CsvPreference getCsvPreference() {
        return csvPreference;
    }

    public void setCsvPreference(CsvPreference csvPreference) {
        this.csvPreference = csvPreference;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
}
