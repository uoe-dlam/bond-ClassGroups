package au.edu.bond.classgroups.spring;

import au.edu.bond.classgroups.dao.TaskDAO;
import au.edu.bond.classgroups.model.Task;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Shane Argo on 11/06/2014.
 */
public class TaskScope implements Scope {

    AtomicReference<Task> currentTask = new AtomicReference<Task>(null);
    ConcurrentHashMap<String,Object> taskScope = null;
    ConcurrentHashMap<String,List<Runnable>> destructionCallbacks = null;

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if(taskScope != null) {
            synchronized (this) {
                if(taskScope != null) {
                    Object object = taskScope.get(name);

                    if (object == null) {
                        object = objectFactory.getObject();
                        taskScope.put(name, object);
                    }
                    return object;
                }
            }
        }
        throw new RuntimeException("No current task");
    }

    @Override
    public Object remove(String name) {
        if(taskScope != null) {
            synchronized (this) {
                if (taskScope != null) {
                    Object object = taskScope.get(name);

                    if (object != null) {
                        if (!taskScope.remove(name, object)) {
                            return null;
                        }
                    }

                    return object;
                }
            }
        }
        throw new RuntimeException("No current task");
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        if(destructionCallbacks != null) {
            synchronized (this) {
                if(destructionCallbacks != null) {
                    List<Runnable> callbacks = destructionCallbacks.get(name);

                    if(callbacks == null) {
                        callbacks =  new CopyOnWriteArrayList<Runnable>();
                        destructionCallbacks.putIfAbsent(name, callbacks);
                    }

                    callbacks.add(callback);
                    return;
                }
            }
        }
        throw new RuntimeException("No current task");
    }

    @Override
    public Object resolveContextualObject(String key) {
        return currentTask.get();
    }

    @Override
    public String getConversationId() {
        final Task task = currentTask.get();
        if(task == null) {
            throw new RuntimeException("No current task");
        }
        return task.getId().toString();
    }

    public void taskStopped(Task task) {
        if(taskScope != null && destructionCallbacks != null && currentTask.get().equals(task)) {
            synchronized (this) {
                if(taskScope != null && destructionCallbacks != null && currentTask.get().equals(task)) {
                    ConcurrentHashMap<String, Object> oldTaskScope = new ConcurrentHashMap<String, Object>(taskScope);
                    taskScope = null;

                    for(String name : oldTaskScope.keySet()) {
                        List<Runnable> callbacks = destructionCallbacks.get(name);
                        for(Runnable callback : callbacks) {
                            callback.run();
                        }
                    }
                    destructionCallbacks = null;
                    currentTask.set(null);
                }
            }
        }
    }

    public void taskStarted(Task task) {
        if(taskScope == null && destructionCallbacks == null) {
            synchronized (this) {
                if(taskScope == null && destructionCallbacks == null) {
                    taskScope = new ConcurrentHashMap<String, Object>();
                    destructionCallbacks = new ConcurrentHashMap<String, List<Runnable>>();

                    this.currentTask.set(task);
                }
            }
        }
    }
}
