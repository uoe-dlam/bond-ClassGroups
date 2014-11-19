package au.edu.bond.classgroups.exception;

/**
 * Created by Shane Argo on 27/06/2014.
 */
public class InvalidPasskeyException extends Exception {

    public InvalidPasskeyException() {
    }

    public InvalidPasskeyException(String message) {
        super(message);
    }

    public InvalidPasskeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPasskeyException(Throwable cause) {
        super(cause);
    }

    public InvalidPasskeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
