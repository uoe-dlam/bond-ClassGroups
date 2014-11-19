package au.edu.bond.classgroups.logging;

import au.edu.bond.classgroups.dao.LogEntryDAO;
import au.edu.bond.classgroups.model.LogEntry;
import au.edu.bond.classgroups.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Date;

/**
 * Created by Shane Argo on 11/06/2014.
 */
public class HibernateTaskLogger extends TaskLogger {

    @Autowired
    LogEntryDAO logEntryDAO;
    @Autowired
    ResourceService resourceService;

    @Override
    public void log(LogEntry.Level level, String msg) {
        log(level, msg, null);
    }

    @Override
    public void log(LogEntry.Level level, String msg, Exception ex) {
        LogEntry entry = new LogEntry();
        entry.setLevel(level);
        entry.setMessage(resourceService.getLocalisationString(msg));
        entry.setDate(new Date());
        entry.setTaskId(getTask().getId());

        if(ex != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(baos);
            ex.printStackTrace(pw);
            pw.close();
            try {
                entry.setStacktrace(baos.toString("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        logEntryDAO.create(entry);
    }

    public LogEntryDAO getLogEntryDAO() {
        return logEntryDAO;
    }

    public void setLogEntryDAO(LogEntryDAO logEntryDAO) {
        this.logEntryDAO = logEntryDAO;
    }
}
