package pt.upa.shared.exception;

/**
 * Base class for exceptions
 */
public class InvalidTransporterNameException extends RuntimeException {
    public InvalidTransporterNameException(String msg) {
        super(msg);
    }

    public InvalidTransporterNameException() {
        super();
    }
}
