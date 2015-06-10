package au.edu.bond.classgroups.logging;

import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.ResourceService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OutputStreamTaskLoggerTest {

    Task task;
    ByteArrayOutputStream outputStream;
    OutputStreamTaskLogger logger;
    ResourceService resourceService;

    @Before
    public void setUp() throws Exception {
        task = new Task();
        task.setId(24L);

        outputStream = new ByteArrayOutputStream();

        resourceService = mock(ResourceService.class);
        when(resourceService.getLocalisationString("Test message")).thenReturn("Test message");

        logger = new OutputStreamTaskLogger();
        logger.setOutputStream(outputStream);
        logger.setTask(task);

        logger.setResourceService(resourceService);
    }

    @After
    public void tearDown() throws Exception {
        outputStream.close();
    }

    @Test
    public void testLog_withMessage_expectPrinted() throws Exception {
        String expectedOutput = String.format("ERROR in task 24: Test message%n");

        logger.error("Test message");

        String result = outputStream.toString("UTF-8");
        assertEquals(expectedOutput, result);
    }

    @Test
    public void testLog_withMessageAndException_expectPrinted() throws Exception {
        String expectedOutput = String.format("ERROR in task 24: Test message [java.lang.Exception: Exception message]%n");

        Exception exception = new Exception("Exception message");
        logger.error("Test message", exception);

        String result = outputStream.toString("UTF-8");
        assertEquals(expectedOutput, result);
    }
}