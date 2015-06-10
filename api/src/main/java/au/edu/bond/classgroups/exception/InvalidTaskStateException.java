package au.edu.bond.classgroups.exception;

/**
 * Created by Shane Argo on 12/06/2014.
 */
public class InvalidTaskStateException extends Exception {

    public InvalidTaskStateException() {
    }

    public InvalidTaskStateException(String message) {
        super(message);
    }

    public InvalidTaskStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTaskStateException(Throwable cause) {
        super(cause);
    }

    public InvalidTaskStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
