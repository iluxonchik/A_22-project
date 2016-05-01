package pt.upa.transporter.exception;

/**
 * Exception thrown when a Job transition is invalid
 */
public class InvalidJobStateTransitionException extends TransporterException {
    public InvalidJobStateTransitionException() {
        super();
    }

    public InvalidJobStateTransitionException(String msg) {
        super(msg);
    }
}
