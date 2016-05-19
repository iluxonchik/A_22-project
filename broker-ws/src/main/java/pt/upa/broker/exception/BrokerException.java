package pt.upa.broker.exception;

/**
 * Base exception class for TransporterClient
 */
public class BrokerException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 201605102234L;

	public BrokerException(String msg) {
        super(msg);
    }

    public BrokerException() {
        super();
    }
}
