package au.edu.bond.classgroups.logging;

import au.edu.bond.classgroups.model.Task;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.concurrent.ExecutionException;

/**
 * Created by shane on 8/09/2015.
 */
public class TaskLoggerFactory {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    LoadingCache<Task, TaskLogger> cache;

    public TaskLoggerFactory(String cacheSpec) {
        cache = CacheBuilder.from(cacheSpec).build(new CacheLoader<Task, TaskLogger>() {
            @Override
            public TaskLogger load(Task task) throws Exception {
                HibernateTaskLogger taskLogger = new HibernateTaskLogger();
                beanFactory.autowireBean(taskLogger);
                taskLogger.setTask(task);
                return taskLogger;
            }
        });
    }

    public TaskLogger getLogger(Task task) {
        try {
            return cache.get(task);
        } catch (ExecutionException e) {
            return null;
        }
    }
}
