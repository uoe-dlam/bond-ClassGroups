package au.edu.bond.classgroups.task;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Shane Argo on 7/07/2014.
 */
public class TaskCleanupRunner implements Runnable {

    @Autowired
    private TaskService taskService;
    @Autowired
    private Configuration configuration;

    @Override
    public void run() {
        if(configuration.isAutoCleanUpOldTasks()) {
            taskService.deleteOlderThan(configuration.getCleanUpDaysToKeep());
        }
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
