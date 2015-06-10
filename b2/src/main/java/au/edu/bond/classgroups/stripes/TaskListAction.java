package au.edu.bond.classgroups.stripes;

import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.TaskService;
import au.edu.bond.classgroups.util.EqualityUtil;
import com.alltheducks.bb.stripes.EntitlementRestrictions;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.integration.spring.SpringBean;

import java.util.*;

/**
 * Created by Shane Argo on 3/06/2014.
 */
@EntitlementRestrictions(entitlements={"bond.classgroups.admin.MODIFY"}, errorPage="/noaccess.jsp")
public class TaskListAction implements ActionBean {

    private ActionBeanContext context;

    private TaskService taskService;

    private List<Task> tasks;

    @DefaultHandler
    public Resolution listTasks() {
        return new ForwardResolution("/WEB-INF/jsp/tasklist.jsp");
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    @SpringBean
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public int getTaskCount() {
        return getTasks().size();
    }

    public List<Task> getTasks() {
        if(tasks == null) {
            tasks = new ArrayList<Task>(taskService.getAll());
        }
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Comparator<Task> getIdComparator() {
        return new IdComparator();
    }

    public class IdComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return Long.compare(o1.getId(), o2.getId());
        }
    }

    public Comparator<Task> getEnteredComparator() {
        return new EnteredComparator();
    }

    public class EnteredComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return EqualityUtil.nullSafeCompare(o1.getEnteredDate(), o2.getEnteredDate());
        }
    }

    public Comparator<Task> getStartedComparator() {
        return new StartedComparator();
    }

    public class StartedComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return EqualityUtil.nullSafeCompare(o1.getStartedDate(), o2.getStartedDate());
        }
    }

    public Comparator<Task> getScheduledComparator() {
        return new ScheduledComparator();
    }

    public class ScheduledComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return EqualityUtil.nullSafeCompare(o1.getScheduledDate(), o2.getScheduledDate());
        }
    }

    public Comparator<Task> getEndedComparator() {
        return new EndedComparator();
    }

    public class EndedComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return EqualityUtil.nullSafeCompare(o1.getEndedDate(), o2.getEndedDate());
        }
    }

    public Comparator<Task> getStatusComparator() {
        return new StatusComparator();
    }

    public class StatusComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getStatus() == null ? 1 :
                    o2.getStatus() == null ? -1 :
                    o1.getStatus().name().compareTo(o2.getStatus().name());
        }
    }

    public Comparator<Task> getEnteredNodeComparator() {
        return new EnteredNodeComparator();
    }

    public class EnteredNodeComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getEnteredNode() == null ? 1 :
                    o2.getEnteredNode() == null ? -1 :
                    o1.getEnteredNode().compareTo(o2.getEnteredNode());
        }
    }

    public Comparator<Task> getProcessingNodeComparator() {
        return new ProcessingNodeComparator();
    }

    public class ProcessingNodeComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getProcessingNode() == null ? 1 :
                    o2.getProcessingNode() == null ? -1 :
                            o1.getProcessingNode().compareTo(o2.getProcessingNode());
        }
    }

    public Comparator<Task> getTotalGroupsComparator() {
        return new TotalGroupsComparator();
    }

    public class TotalGroupsComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getTotalGroups() == null ? 1 :
                    o2.getTotalGroups() == null ? -1 :
                    o1.getTotalGroups().compareTo(o2.getTotalGroups());
        }
    }

    public Comparator<Task> getProcessedGroupsComparator() {
        return new ProcessedGroupsComparator();
    }

    public class ProcessedGroupsComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getProcessedGroups() == null ? 1 :
                    o2.getProcessedGroups() == null ? -1 :
                    o1.getProcessedGroups().compareTo(o2.getProcessedGroups());
        }
    }

}
