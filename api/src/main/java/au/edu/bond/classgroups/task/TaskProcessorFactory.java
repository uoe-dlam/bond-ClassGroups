package au.edu.bond.classgroups.task;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class TaskProcessorFactory {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    public TaskProcessor getDefault() {
        TaskProcessor taskProcessor = new TaskProcessor();
        beanFactory.autowireBean(taskProcessor);
        return taskProcessor;
    }

}
