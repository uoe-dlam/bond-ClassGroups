package au.edu.bond.classgroups.feed;

import au.edu.bond.classgroups.feed.csv.CsvFeedDeserialiser;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.model.Group;
import au.edu.bond.classgroups.service.ResourceService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CsvFeedDeserialiserTest {

    CsvFeedDeserialiser csvFeedDeserialiser;
    TaskLogger logger;
    ResourceService resourceService;

    @Before
    public void setUp() throws Exception {
        logger = mock(TaskLogger.class);
        resourceService = mock(ResourceService.class);

        when(resourceService.getLocalisationString(anyString(), anyCollection())).thenReturn("blahblah");

        csvFeedDeserialiser = new CsvFeedDeserialiser();
        csvFeedDeserialiser.setCurrentTaskLogger(logger);
        csvFeedDeserialiser.setResourceService(resourceService);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetGroups_withEmptyStream_expectLoggerError() throws Exception {
        InputStream groupsIS = new ByteArrayInputStream("".getBytes());
        InputStream membersIS = new ByteArrayInputStream("".getBytes());

        csvFeedDeserialiser.setGroupsInputStream(groupsIS);
        csvFeedDeserialiser.setMembersInputStream(membersIS);

        Collection<Group> result = csvFeedDeserialiser.getGroups();

        assertNull(result);
        verify(logger, times(2)).error(anyString());
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void testGetGroups_withOnlyHeaders_expectEmptyList() throws Exception {
        InputStream groupsIS = new ByteArrayInputStream("groupId,courseId,title".getBytes());
        InputStream membersIS = new ByteArrayInputStream("groupId,userId".getBytes());

        csvFeedDeserialiser.setGroupsInputStream(groupsIS);
        csvFeedDeserialiser.setMembersInputStream(membersIS);

        Collection<Group> result = csvFeedDeserialiser.getGroups();

        assertEquals(0, result.size());
    }

    @Test
    public void testGetGroups_withOneGroupAndOneMember_expectOneGroup() throws Exception {
        InputStream groupsIS = new ByteArrayInputStream("groupId,courseId,title\ngroup1,course1,A Group".getBytes());
        InputStream membersIS = new ByteArrayInputStream("groupId,userId\ngroup1,user1".getBytes());

        csvFeedDeserialiser.setGroupsInputStream(groupsIS);
        csvFeedDeserialiser.setMembersInputStream(membersIS);

        Collection<Group> result = csvFeedDeserialiser.getGroups();

        assertEquals(1, result.size());
    }

    @Test
    public void testGetGroups_withOneGroupAndOneMemberAndInvalidMember_expectOneGroupAndWarning() throws Exception {
        InputStream groupsIS = new ByteArrayInputStream("groupId,courseId,title\ngroup1,course1,A Group".getBytes());
        InputStream membersIS = new ByteArrayInputStream("groupId,userId\ngroup1,user1\ngroup2,user1".getBytes());

        csvFeedDeserialiser.setGroupsInputStream(groupsIS);
        csvFeedDeserialiser.setMembersInputStream(membersIS);

        Collection<Group> result = csvFeedDeserialiser.getGroups();

        assertEquals(1, result.size());
        verify(logger).warning(anyString());
    }

    @Test
    public void testGetGroups_withMultipleGroupAndMembers_expectMultipleGroupsAndMembers() throws Exception {
        InputStream groupsIS = new ByteArrayInputStream(("groupId,courseId,title\n" +
                "group1,course1,A Group\n" +
                "group2,course1,Another Group\n" +
                "group3,course2,And another\n").getBytes());
        InputStream membersIS = new ByteArrayInputStream(("groupId,userId\n" +
                "group1,user1\n" +
                "group2,user1\n" +
                "group2,user2\n" +
                "group3,user1\n").getBytes());

        csvFeedDeserialiser.setGroupsInputStream(groupsIS);
        csvFeedDeserialiser.setMembersInputStream(membersIS);

        Collection<Group> result = csvFeedDeserialiser.getGroups();

        Group group2 = null;
        for(Group group: result) {
            if(group.getGroupId().equals("group2")) {
                group2 = group;
                break;
            }
        }

        assertEquals(3, result.size());
        assertEquals(2, group2.getMembers().size());
    }


}