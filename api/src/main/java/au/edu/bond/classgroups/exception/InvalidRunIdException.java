package au.edu.bond.classgroups.exception;

/**
 * Created by shane on 4/09/2015.
 */
public class InvalidRunIdException extends Exception {

    public InvalidRunIdException() {
    }

    public InvalidRunIdException(String message) {
        super(message);
    }

    public InvalidRunIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRunIdException(Throwable cause) {
        super(cause);
    }

    public InvalidRunIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
