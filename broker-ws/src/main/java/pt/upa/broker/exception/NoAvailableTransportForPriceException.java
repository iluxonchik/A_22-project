package pt.upa.broker.exception;

public final class NoAvailableTransportForPriceException extends NoAvailableTransportException {
    private static final String DEFAULT_MSG = "No available transport from the specified origin to the specified " +
            "destination for up to the specified maximum price found."; // maybe add more uses of "specified"?

    public NoAvailableTransportForPriceException(String msg) { super(msg); }

    public NoAvailableTransportForPriceException() { super(DEFAULT_MSG); }

    public NoAvailableTransportForPriceException(String origin, String destination, float price) {
        super("No available transport from " + origin + " to "
                + destination + " for a price up to " + price + " found.");
    }

}
