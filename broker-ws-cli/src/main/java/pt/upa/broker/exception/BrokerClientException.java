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
}