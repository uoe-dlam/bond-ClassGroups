package au.edu.bond.classgroups.config;

import au.edu.bond.classgroups.config.model.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for loading and persisting configuration for the building block.
 * Created by Shane Argo on 23/05/14.
 */
public abstract class ConfigurationService {

    List<Runnable> reloadListeners = new ArrayList<Runnable>();

    public abstract Configuration loadConfiguration();

    public abstract void persistConfiguration(Configuration configuration);

    public abstract ConfigurationMonitor getMonitor();

    public void reload() {
        for(Runnable listener : reloadListeners) {
            listener.run();
        }
    }

    public void registerReloadListener(Runnable runnable) {
        reloadListeners.add(runnable);
    }

    public void unregisterReloadListener(Runnable runnable) {
        reloadListeners.remove(runnable);
    }

    public void clearReloadListeners() {
        reloadListeners = new ArrayList<Runnable>();
    }

}
