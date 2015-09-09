package au.edu.bond.classgroups.feed;

import java.io.*;

/**
 * Created by shane on 8/09/2015.
 */
public class FileFeedFetcher extends FeedFetcher {

    private File groupsFile;
    private File membersFile;

    public FileFeedFetcher(File groupsFile, File membersFile) {
        this.groupsFile = groupsFile;
        this.membersFile = membersFile;
    }

    @Override
    public InputStream fetchGroupsData() throws IOException {
        return new FileInputStream(groupsFile);
    }

    @Override
    public InputStream fetchMembersData() throws IOException{
        return new FileInputStream(membersFile);
    }
}
