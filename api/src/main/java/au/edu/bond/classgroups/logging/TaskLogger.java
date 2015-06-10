package au.edu.bond.classgroups.logging;

import au.edu.bond.classgroups.model.LogEntry;
import au.edu.bond.classgroups.model.Task;

/**
 * Created by Shane Argo on 2/06/2014.
 */
public abstract class TaskLogger {

    Task task;

    public abstract void log(LogEntry.Level level, String msg);
    public abstract void log(LogEntry.Level level, String msg, Exception ex);

    public void error(String msg) {
        log(LogEntry.Level.ERROR, msg);
    }

    public void error(String msg, Exception ex) {
        log(LogEntry.Level.ERROR, msg, ex);
    }

    public void warning(String msg) {
        log(LogEntry.Level.WARNING, msg);
    }

    public void warning(String msg, Exception ex) {
        log(LogEntry.Level.WARNING, msg, ex);
    }

    public void info(String msg) {
        log(LogEntry.Level.INFO, msg);
    }

    public void info(String msg, Exception ex) {
        log(LogEntry.Level.INFO, msg, ex);
    }



    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
