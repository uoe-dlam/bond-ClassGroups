package au.edu.bond.classgroups.feed;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by shane on 8/09/2015.
 */
public class UrlFeedFetcher extends FeedFetcher {

    private String groupsUrl;
    private String membersUrl;

    public UrlFeedFetcher(String groupsUrl, String membersUrl) {
        this.groupsUrl = groupsUrl;
        this.membersUrl = membersUrl;
    }

    @Override
    public InputStream fetchGroupsData() throws IOException {
        return new URL(groupsUrl).openStream();
    }

    @Override
    public InputStream fetchMembersData() throws IOException {
        return new URL(membersUrl).openStream();
    }

}
