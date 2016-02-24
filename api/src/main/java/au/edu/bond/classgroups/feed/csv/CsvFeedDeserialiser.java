package au.edu.bond.classgroups.feed.csv;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.config.FeedHeaderConfig;
import au.edu.bond.classgroups.exception.FeedDeserialisationException;
import au.edu.bond.classgroups.feed.FeedDeserialiser;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.model.Group;
import au.edu.bond.classgroups.model.Member;
import au.edu.bond.classgroups.service.ResourceService;
import com.google.common.collect.Sets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
    private ResourceService resourceService;
    @Autowired
    Configuration configuration;

    private InputStream groupsInputStream;
    private InputStream membersInputStream;

    @Override
    public Collection<Group> getGroups(TaskLogger taskLogger) throws FeedDeserialisationException {
        //Map instead of list/collection so that the groups can be easily found again during member processing.
        Map<String, Group> groups = new HashMap<String, Group>();

        try(final CSVParser groupsParser = CSVFormat.DEFAULT.withHeader().parse(new InputStreamReader(groupsInputStream));
            final CSVParser membersParser = CSVFormat.DEFAULT.withHeader().parse(new InputStreamReader(membersInputStream))) {

            boolean stop = false;
            if (!groupsParser.getHeaderMap().keySet().containsAll(configuration.getFeedHeaderConfig().getRequiredGroupsFeedHeaders())) {
                taskLogger.error("bond.classgroups.error.missingheadergroups");
                stop = true;
            }
            if (!membersParser.getHeaderMap().keySet().containsAll(configuration.getFeedHeaderConfig().getRequiredMembersFeedHeaders())) {
                taskLogger.error("bond.classgroups.error.missingheadermembers");
                stop = true;
            }
            if (stop) {
                return null;
            }

            for (CSVRecord record : groupsParser) {
                final Group group = translateRecordToGroup(record);
                groups.put(group.getGroupId(), group);

                if (Thread.currentThread().isInterrupted()) {
                    taskLogger.error("bond.classgroups.error.groupsthreadinterrupted");
                    throw new FeedDeserialisationException(resourceService.getLocalisationString(
                            "bond.classgroups.exception.groupsthreadinterrupted"));
                }
            }

            for (CSVRecord record : membersParser) {
                final String groupId = record.get(configuration.getFeedHeaderConfig().getGroupIdHeader());
                Group group = groups.get(groupId);

                if (group == null) {
                    taskLogger.warning(resourceService.getLocalisationString(
                            "bond.classgroups.warning.memberformissinggroup", groupId));
                    continue;
                }

                Collection<Member> members = group.getMembers();
                if (members == null) {
                    members = new HashSet<Member>();
                    group.setMembers(members);
                }

                members.add(translateRecordToMember(record));

                if(Thread.currentThread().isInterrupted()) {
                    taskLogger.error("bond.classgroups.error.membersthreadinterrupted");
                    throw new FeedDeserialisationException(resourceService.getLocalisationString(
                            "bond.classgroups.exception.membersthreadinterrupted"));
                }
            }

        } catch (IOException e) {
            taskLogger.error("bond.classgroups.error.failedtoparsecsv", e);
            throw new FeedDeserialisationException(resourceService.getLocalisationString(
                    "bond.classgroups.exception.failedtoparsecsv"), e);
        }

        return groups.values();
    }

    private Group translateRecordToGroup(CSVRecord record) {
        Group group = new Group();

        final FeedHeaderConfig headers = configuration.getFeedHeaderConfig();
        group.setCourseId(record.get(headers.getCourseIdHeader()));
        group.setGroupId(record.get(headers.getGroupIdHeader()));
        group.setTitle(record.get(headers.getTitleHeader()));

        if(record.isSet(headers.getLeaderHeader())) {
            group.setLeaderId(record.get(headers.getLeaderHeader()));
        }
        if(record.isSet(headers.getGroupSetHeader())) {
            group.setGroupSet(record.get(headers.getGroupSetHeader()));
        }

        if(record.isSet(headers.getAvailableHeader())) {
            final String availableStr = record.get(headers.getAvailableHeader());
            final boolean available = Boolean.parseBoolean(availableStr);
            group.setAvailable(available);
        }

        if(record.isSet(headers.getToolsHeader())) {
            final String toolsStr = record.get(headers.getToolsHeader());
            if (!StringUtils.isEmpty(toolsStr)) {
                final HashSet<String> tools = Sets.newHashSet(StringUtils.split(toolsStr, "|"));
                group.setTools(tools);
            }
        }

        return group;
    }

    private Member translateRecordToMember(CSVRecord record) {
        Member member = new Member();

        member.setUserId(record.get(configuration.getFeedHeaderConfig().getUserIdHeader()));

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

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
