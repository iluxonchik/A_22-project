package pt.upa.transporter.exception;

/**
 * A base class for Transporter exceptions
 */
public class TransporterException extends RuntimeException {
    public TransporterException(String msg) {
        super(msg);
    }

    public TransporterException() {
        super();
    }
}
