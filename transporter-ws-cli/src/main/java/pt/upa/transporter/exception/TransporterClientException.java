package pt.upa.transporter.exception;

/**
 * Base exception class for TransporterClient
 */
public class TransporterClientException extends RuntimeException {
    public TransporterClientException(String msg) { super(msg); }
    public TransporterClientException() { super(); }
}
