package pt.upa.broker.exception;

/**
 * Base exception class for TransporterClient
 */
public class BrokerException extends RuntimeException {
    public BrokerException(String msg) {
        super(msg);
    }

    public BrokerException() {
        super();
    }
}
