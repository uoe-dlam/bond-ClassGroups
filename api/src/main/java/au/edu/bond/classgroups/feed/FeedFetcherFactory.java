package au.edu.bond.classgroups.feed;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.service.DirectoryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * Created by shane on 8/09/2015.
 */
public class FeedFetcherFactory {

    private Configuration configuration;
    private DirectoryFactory directoryFactory;

    public FeedFetcher getConfiguredFeedFetcher() {
        switch (configuration.getFeedFetcherType()) {
            case URL:
                final UrlFeedFetcher urlFeedFetcher = new UrlFeedFetcher(configuration.getPullUrlCsvFeedDeserialiser().getGroupsUrl(), configuration.getPullUrlCsvFeedDeserialiser().getMembersUrl());
                urlFeedFetcher.setDirectoryFactory(directoryFactory);
                return urlFeedFetcher;

            case FILE:
                final FileFeedFetcher fileFeedFetcher = new FileFeedFetcher(
                        new File(configuration.getPullFileCsvFeedDeserialiser().getGroupsFilePath()),
                        new File(configuration.getPullFileCsvFeedDeserialiser().getMembersFilePath()));
                fileFeedFetcher.setDirectoryFactory(directoryFactory);
                return fileFeedFetcher;
        }

        return null;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public DirectoryFactory getDirectoryFactory() {
        return directoryFactory;
    }

    public void setDirectoryFactory(DirectoryFactory directoryFactory) {
        this.directoryFactory = directoryFactory;
    }
}
