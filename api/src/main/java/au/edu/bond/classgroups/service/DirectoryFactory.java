package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.model.Task;

import java.io.File;
import java.util.Date;

/**
 * Created by Shane Argo on 12/06/2014.
 */
public abstract class DirectoryFactory {

    public abstract File getConfigurationDirectory();

    public File getHttpPushBaseDirectory() {
        final File file = new File(getConfigurationDirectory(), "httppush");
        file.mkdirs();
        return file;
    }

    public File getHttpPushDir(String runId) {
        final File file = new File(getHttpPushBaseDirectory(), runId);
        file.mkdirs();
        return file;
    }

    public File getFeedBaseDirectory() {
        final File file = new File(getConfigurationDirectory(), "feed");
        file.mkdirs();
        return file;
    }

    public File getFeedDirectory(Task task) {
        return getFeedDirectory(task.getId());
    }

    public File getFeedDirectory(long taskId) {
        final File file = new File(getFeedBaseDirectory(), String.format("task%s", Long.toString(taskId)));
        file.mkdirs();
        return file;
    }

    public File getFeedGroupsFile(Task task) {
        final File file = new File(getFeedDirectory(task), "groups");
        return file;
    }

    public File getFeedMembersFile(Task task) {
        final File file = new File(getFeedDirectory(task), "members");
        return file;
    }

}
