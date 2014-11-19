package au.edu.bond.classgroups.logging;

import au.edu.bond.classgroups.model.LogEntry;
import au.edu.bond.classgroups.service.ResourceService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Shane Argo on 2/06/2014.
 */
public class OutputStreamTaskLogger extends TaskLogger {

    @Autowired
    private ResourceService resourceService;

    OutputStream outputStream;

    @Override
    public void log(LogEntry.Level level, String msg) {
        String line = String.format("%s in task %s: %s%n",
                level.toString(),
                task.getId(),
                resourceService.getLocalisationString(msg));
        try {
            IOUtils.write(line, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(LogEntry.Level level, String msg, Exception ex) {
        String line = String.format("%s in task %s: %s [%s]%n",
                level.toString(),
                task.getId(),
                resourceService.getLocalisationString(msg),
                ex);
        try {
            IOUtils.write(line, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
}
