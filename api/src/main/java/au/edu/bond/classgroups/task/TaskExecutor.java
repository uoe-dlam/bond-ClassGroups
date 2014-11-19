package au.edu.bond.classgroups.task;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shane Argo on 30/06/2014.
 */
public class TaskExecutor implements Closeable {

    public static final long SHUTDOWN_TIMEOUT_MILLISECONDS = 30000;

    private ExecutorService executorService;

    public TaskExecutor() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public void executeTaskProcessor(TaskProcessor taskProcessor) {
        executorService.submit(taskProcessor);
    }

    @Override
    public void close() throws IOException {
        if(executorService != null) {
            executorService.shutdownNow();

            boolean success = false;
            try {
                success = executorService.awaitTermination(SHUTDOWN_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!success) {
                System.out.println("Groups task failed to complete processing before termination.");
            }
        }
    }
}
