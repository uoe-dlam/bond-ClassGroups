package au.edu.bond.classgroups.config;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * Created by Shane Argo on 25/06/2014.
 */
public class WatchServiceConfigurationMonitor extends ConfigurationMonitor {

    private File configurationFile;

    private volatile WatchService watchService;

    public WatchServiceConfigurationMonitor(File configurationFile, ConfigurationService configurationService) {
        super(configurationService);
        this.configurationFile = configurationFile;
    }

    @Override
    public void run() {
        Path path = configurationFile.getParentFile().toPath();

        try {
            watchService = path.getFileSystem().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException("Failed to obtain watch service for configuration file.", e);
        }


        while(!Thread.currentThread().isInterrupted()) {
            WatchKey watchKey;
            try {
                watchKey = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (ClosedWatchServiceException e) {
                Thread.currentThread().interrupt();
                break;
            }

            for (WatchEvent event : watchKey.pollEvents()) {
                if(event.context().toString().equals(configurationFile.getName())) {
                    getConfigurationService().reload();
                }
            }

            if(!watchKey.reset()){
                break;
            }
        }
    }

    @Override
    public void close() throws IOException {
        if(watchService != null) {
            watchService.close();
        }
    }

    public File getConfigurationFile() {
        return configurationFile;
    }
}
