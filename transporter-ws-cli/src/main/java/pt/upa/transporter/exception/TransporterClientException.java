package pt.upa.transporter.exception;

/**
 * Base exception class for TransporterClient
 */
public class TransporterClientException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TransporterClientException(String msg) {
        super(msg);
    }

    public TransporterClientException() {
        super();
    }

    public TransporterClientException(Throwable cause) {
        super(cause);
    }

    public TransporterClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
