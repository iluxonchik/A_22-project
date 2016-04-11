package pt.upa.broker.exception;

public final class DestinationUnknownException extends BrokerException {
    private static final String DEFAULT_MSG = "The provided destination is unknown.";

    public DestinationUnknownException(String msg) { super(msg); }

    public DestinationUnknownException() { super(DEFAULT_MSG); }
}