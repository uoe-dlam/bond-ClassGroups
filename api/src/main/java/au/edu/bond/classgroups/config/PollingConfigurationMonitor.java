package au.edu.bond.classgroups.config;

import java.io.IOException;
import java.io.File;


/**
 * Created by Shane Argo on 18/09/2014.
 */
public class PollingConfigurationMonitor extends ConfigurationMonitor {

    private File configurationFile;
    private int pollFreq;

    private long lastReload = 0;

    public PollingConfigurationMonitor(int pollFreq, File configurationFile, ConfigurationService configurationService) {
        super(configurationService);
        this.configurationFile = configurationFile;
        this.pollFreq = pollFreq;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            if(configurationFile.lastModified() > lastReload) {
                this.getConfigurationService().reload();
                lastReload = configurationFile.lastModified();
            }

            try {
                Thread.sleep(pollFreq);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void close() throws IOException {

    }
}
