package au.edu.bond.classgroups.task;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.manager.ScheduleManager;
import com.alltheducks.configutils.monitor.ConfigurationChangeListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Shane Argo on 8/07/2014.
 */
public class ScheduledTaskConfigurationChangeListener implements ConfigurationChangeListener<Configuration>, Closeable {

    @Autowired
    private ScheduleManager scheduleManager;

    @Override
    public void configurationChanged(Configuration configuration) {
        scheduleManager.updateSchedules();
    }

    @Override
    public void close() throws IOException {
        scheduleManager.close();
    }

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }

    public void setScheduleManager(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
    }
}
