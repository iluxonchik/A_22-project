package pt.upa.broker.exception;

/**
 * Base exception class for TransporterClient
 */
public class BrokerClientException extends RuntimeException {
    public BrokerClientException(String msg) {
        super(msg);
    }

    public BrokerClientException() {
        super();
    }

    public BrokerClientException(Throwable cause) {
        super(cause);
    }

    public BrokerClientException(String message, Throwable cause) {
        super(message, cause);
    }

}