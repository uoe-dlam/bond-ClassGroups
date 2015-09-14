package au.edu.bond.classgroups.feed;

import au.edu.bond.classgroups.feed.csv.FileCsvFeedDeserialiser;
import au.edu.bond.classgroups.task.TaskProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class FeedDeserialiserFactory {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    public FileCsvFeedDeserialiser getFileCsvFeedDeserialiser() {
        FileCsvFeedDeserialiser fileCsvFeedDeserialiser = new FileCsvFeedDeserialiser();
        beanFactory.autowireBean(fileCsvFeedDeserialiser);
        return fileCsvFeedDeserialiser;
    }

}
