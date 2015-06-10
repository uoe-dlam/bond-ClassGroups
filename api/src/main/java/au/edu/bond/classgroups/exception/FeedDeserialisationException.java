package au.edu.bond.classgroups.exception;

/**
 * Created by Shane Argo on 13/06/2014.
 */
public class FeedDeserialisationException extends Exception {

    public FeedDeserialisationException() {
    }

    public FeedDeserialisationException(String message) {
        super(message);
    }

    public FeedDeserialisationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeedDeserialisationException(Throwable cause) {
        super(cause);
    }

    public FeedDeserialisationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
