package au.edu.bond.classgroups.task;

import au.edu.bond.classgroups.exception.InvalidTaskStateException;
import au.edu.bond.classgroups.logging.TaskLogger;
import au.edu.bond.classgroups.logging.TaskLoggerFactory;
import au.edu.bond.classgroups.manager.ScheduleManager;
import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Shane Argo on 8/07/2014.
 */
public class ScheduledTaskProcessor implements Runnable {

    @Autowired
    private TaskProcessorFactory taskProcessorFactory;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ScheduleManager scheduleManager;
    @Autowired
    private TaskLoggerFactory taskLoggerFactory;

    private Task task;

    @Override
    public void run() {
        try {
            task = taskService.prepareScheduledTask(task);
        } catch (InvalidTaskStateException e) {
            e.printStackTrace();
            return;
        }

        if(task == null) {
            return;
        }

        if(task.getStatus() == Task.Status.SKIPPED) {
            return;
        }

        try {
            TaskProcessor taskProcessor = taskProcessorFactory.getDefault();
            taskProcessor.setTask(task);
            final TaskLogger taskLogger = taskLoggerFactory.getLogger(task);
            taskProcessor.setTaskLogger(taskLogger);

            taskProcessor.run();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            scheduleManager.updateSchedules();
        }
    }

    public TaskProcessorFactory getTaskProcessorFactory() {
        return taskProcessorFactory;
    }

    public void setTaskProcessorFactory(TaskProcessorFactory taskProcessorFactory) {
        this.taskProcessorFactory = taskProcessorFactory;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
