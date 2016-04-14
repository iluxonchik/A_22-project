package pt.upa.transporter;

/**
 * Base exception class for TransporterClient
 */
public class TransporterClientException extends Exception {
    public TransporterClientException(String msg) { super(msg); }
    public TransporterClientException() { super(); }
}
