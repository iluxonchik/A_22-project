package pt.upa.broker.exception;

public class NoAvailableTransportException extends Exception {
    private static final String DEFAULT_MSG = "No available transport from the specified origin " +
                                                                            "to the specified destination found.";

    public NoAvailableTransportException(String msg) { super(msg); }

    public NoAvailableTransportException() { super(DEFAULT_MSG); }

    public NoAvailableTransportException(String origin, String destination) {
        super("No available transport from " + origin + " to " + destination + " found.");
    }

}
