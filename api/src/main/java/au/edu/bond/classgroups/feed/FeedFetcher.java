package au.edu.bond.classgroups.feed;

import au.edu.bond.classgroups.model.Task;
import au.edu.bond.classgroups.service.DirectoryFactory;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * Created by shane on 8/09/2015.
 */
public abstract class FeedFetcher {

    private DirectoryFactory directoryFactory;

    public abstract InputStream fetchGroupsData() throws IOException;
    public abstract InputStream fetchMembersData() throws IOException;

    public void fetchData(Task task) throws IOException {
        final File feedDirectory = directoryFactory.getFeedDirectory(task);

        FileUtils.copyInputStreamToFile(new BufferedInputStream(fetchGroupsData()), new File(feedDirectory, "groups"));
        FileUtils.copyInputStreamToFile(new BufferedInputStream(fetchMembersData()), new File(feedDirectory, "members"));
    }

    public DirectoryFactory getDirectoryFactory() {
        return directoryFactory;
    }

    public void setDirectoryFactory(DirectoryFactory directoryFactory) {
        this.directoryFactory = directoryFactory;
    }
}
