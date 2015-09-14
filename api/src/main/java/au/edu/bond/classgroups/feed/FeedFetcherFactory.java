package au.edu.bond.classgroups.feed;

import au.edu.bond.classgroups.config.Configuration;
import au.edu.bond.classgroups.service.DirectoryFactory;
import com.alltheducks.configutils.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * Created by shane on 8/09/2015.
 */
public class FeedFetcherFactory {

    private ConfigurationService<Configuration> configurationService;
    private DirectoryFactory directoryFactory;

    public FeedFetcher getConfiguredFeedFetcher() {
        final Configuration configuration = configurationService.loadConfiguration();
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

    public ConfigurationService<Configuration> getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService<Configuration> configurationService) {
        this.configurationService = configurationService;
    }

    public DirectoryFactory getDirectoryFactory() {
        return directoryFactory;
    }

    public void setDirectoryFactory(DirectoryFactory directoryFactory) {
        this.directoryFactory = directoryFactory;
    }
}
