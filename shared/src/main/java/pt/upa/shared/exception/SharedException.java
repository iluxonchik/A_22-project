package pt.upa.shared.exception;

/**
 * Base class for exceptions
 */
public class SharedException extends RuntimeException {
    public SharedException(String msg) {
        super(msg);
    }

    public SharedException() {
        super();
    }
}
