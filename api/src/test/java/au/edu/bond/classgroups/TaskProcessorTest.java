package au.edu.bond.classgroups;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.feed.FeedDeserialiser;
import au.edu.bond.classgroups.feed.FeedDeserialiserFactory;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.manager.GroupManager;
import au.edu.bond.classgroups.manager.MemberManager;
import au.edu.bond.classgroups.manager.SmartViewManager;
import au.edu.bond.classgroups.manager.ToolManager;
import au.edu.bond.classgroups.model.Group;
import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.ResourceService;
import au.edu.bond.classgroups.task.TaskProcessor;
import au.edu.bond.classgroups.service.TaskService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.mockito.Mockito.*;

public class TaskProcessorTest {

    TaskService taskService;
    FeedDeserialiserFactory feedDeserialiserFactory;
    Task currentTask;
    TaskLogger currentTasklogger;
    GroupManager groupManager;
    MemberManager memberManager;
    ToolManager toolManager;
    SmartViewManager smartViewManager;
    Configuration configuration;
    ResourceService resourceService;

    FeedDeserialiser feedDeserialiser;
    TaskProcessor taskProcessor;

    @Before
    public void setUp() throws Exception {
        configuration = new Configuration();
        configuration.setToolsMode(Configuration.ToolsMode.CREATE);

        taskService = mock(TaskService.class);

        currentTask = new Task();
        currentTask.setId(1L);
        currentTask.setStatus(Task.Status.PENDING);

        feedDeserialiser = mock(FeedDeserialiser.class);
        feedDeserialiserFactory = mock(FeedDeserialiserFactory.class);
        when(feedDeserialiserFactory.getDefault()).thenReturn(feedDeserialiser);

        resourceService = mock(ResourceService.class);
        when(resourceService.getLocalisationString(anyString(), anyCollection())).thenReturn("blahblah");

        currentTasklogger = mock(TaskLogger.class);
        groupManager = mock(GroupManager.class);
        memberManager = mock(MemberManager.class);
        toolManager = mock(ToolManager.class);
        smartViewManager = mock(SmartViewManager.class);

        taskProcessor = new TaskProcessor();
        taskProcessor.setTaskService(taskService);
        taskProcessor.setFeedDeserialiserFactory(feedDeserialiserFactory);
        taskProcessor.setCurrentTask(currentTask);
        taskProcessor.setCurrentTaskLogger(currentTasklogger);
        taskProcessor.setGroupManager(groupManager);
        taskProcessor.setMemberManager(memberManager);
        taskProcessor.setToolManager(toolManager);
        taskProcessor.setSmartViewManager(smartViewManager);
        taskProcessor.setConfiguration(configuration);
        taskProcessor.setResourceService(resourceService);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testRun_withNoGroupsNoMembers_expectNoGroupCreation() throws Exception {
        Collection<Group> groups = new HashSet<Group>();
        when(feedDeserialiser.getGroups()).thenReturn(groups);

        taskProcessor.run();

        verify(feedDeserialiserFactory).getDefault();
        verifyNoMoreInteractions(groupManager);
    }

    @Test
    public void testRun_withSingleGroup_expectOneGroupCreation() throws Exception {
        Collection<Group> groups = new HashSet<Group>();

        Group group = new Group();
        group.setCourseId("myCourse");
        group.setGroupId("myGroup");
        groups.add(group);

        when(feedDeserialiser.getGroups()).thenReturn(groups);

        taskProcessor.run();

        verify(groupManager).syncGroup(group);
        verifyNoMoreInteractions(groupManager);
    }

    @Test
    public void testRun_withMultipleGroups_expectMultipleGroupCreation() throws Exception {
        Collection<Group> groups = new HashSet<Group>();

        Group group1 = new Group();
        group1.setCourseId("myCourse1");
        group1.setGroupId("myGroup1");
        groups.add(group1);

        Group group2 = new Group();
        group2.setCourseId("myCourse2");
        group2.setGroupId("myGroup2");
        groups.add(group2);

        Group group3 = new Group();
        group3.setCourseId("myCourse3");
        group3.setGroupId("myGroup3");
        groups.add(group3);

        when(feedDeserialiser.getGroups()).thenReturn(groups);

        taskProcessor.run();

        verify(groupManager).syncGroup(group1);
        verify(groupManager).syncGroup(group2);
        verify(groupManager).syncGroup(group3);
        verifyNoMoreInteractions(groupManager);
    }

    @Test
    public void testRun_withoutDeserialiser_expectUseOfDefaultDeserialiser() throws Exception {
        Collection<Group> groups = new HashSet<Group>();

        Group group = new Group();
        group.setCourseId("myCourse");
        group.setGroupId("myGroup");
        groups.add(group);

        when(feedDeserialiser.getGroups()).thenReturn(groups);
        when(feedDeserialiserFactory.getDefault()).thenReturn(feedDeserialiser);

        taskProcessor.run();

        verify(groupManager).syncGroup(group);
        verifyNoMoreInteractions(groupManager);
    }

    @Test
    public void testRun_withCustomFeedDeserialiser_noIntetactionWithFactory() throws Exception {

        Collection<Group> groups = new HashSet<Group>();
        when(feedDeserialiser.getGroups()).thenReturn(groups);

        taskProcessor.run();
        taskProcessor.setFeedDeserialiser(feedDeserialiser);

        verify(feedDeserialiserFactory).getDefault();
        verifyNoMoreInteractions(groupManager);

    }
}