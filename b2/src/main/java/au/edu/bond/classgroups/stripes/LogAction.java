package au.edu.bond.classgroups.stripes;

import au.edu.bond.classgroups.dao.LogEntryDAO;
import au.edu.bond.classgroups.model.LogEntry;
import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.TaskService;
import au.edu.bond.classgroups.util.EqualityUtil;
import com.alltheducks.bb.stripes.EntitlementRestrictions;
import com.google.gson.Gson;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.integration.spring.SpringBean;

import java.io.StringReader;
import java.util.*;

/**
 * Created by Shane Argo on 3/06/2014.
 */
@EntitlementRestrictions(entitlements={"bond.classgroups.admin.MODIFY"}, errorPage="/noaccess.jsp")
public class LogAction implements ActionBean {

    private ActionBeanContext context;
    private String pluginsUrl;
    private TaskService taskService;
    private LogEntryDAO logEntryDAO;

    private Long lastEntryId;
    private Long taskId;
    private Task task;

    @DefaultHandler
    public Resolution display() {
        if(taskId == null) {
            try {
                task = taskService.getMostRecentlyStarted();
            } catch (Exception ignored) {
            }
            return new ForwardResolution("/WEB-INF/jsp/recentlog.jsp");
        } else {
            task = taskService.getById(taskId);
            return new ForwardResolution("/WEB-INF/jsp/lookuplog.jsp");
        }
    }

    public Resolution update() {
        task = taskService.getById(taskId);

        List<LogEntry> logEntries;
        if(lastEntryId != null) {
            logEntries = new ArrayList<LogEntry>(logEntryDAO.getAllForTaskAfterId(task, lastEntryId));
        } else {
            logEntries = new ArrayList<LogEntry>(logEntryDAO.getAllForTask(task));
        }
        Collections.sort(logEntries, new LogEntryIdComparator());


        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setTask(task);
        taskStatus.setLogEntries(logEntries);

        Gson gson = new Gson();
        return new StreamingResolution("application/json", new StringReader(gson.toJson(taskStatus)));
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    public String getPluginsUrl() {
        return pluginsUrl;
    }

    @SpringBean
    public void setPluginsUrl(String pluginsUrl) {
        this.pluginsUrl = pluginsUrl;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    @SpringBean
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public LogEntryDAO getLogEntryDAO() {
        return logEntryDAO;
    }

    @SpringBean
    public void setLogEntryDAO(LogEntryDAO logEntryDAO) {
        this.logEntryDAO = logEntryDAO;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getLastEntryId() {
        return lastEntryId;
    }

    public void setLastEntryId(Long lastEntryId) {
        this.lastEntryId = lastEntryId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public class TaskStatus {
        private Task task;
        private List<LogEntry> logEntries;

        public List<LogEntry> getLogEntries() {
            return logEntries;
        }

        public void setLogEntries(List<LogEntry> logEntries) {
            this.logEntries = logEntries;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }
    }

    public class LogEntryDateComparator implements Comparator<LogEntry> {
        @Override
        public int compare(LogEntry o1, LogEntry o2) {
            return EqualityUtil.nullSafeCompare(o1.getDate(), o2.getDate());
        }
    }

    public class LogEntryIdComparator implements Comparator<LogEntry> {
        @Override
        public int compare(LogEntry o1, LogEntry o2) {
            return EqualityUtil.nullSafeCompare(o1.getId(), o2.getId());
        }
    }
}
