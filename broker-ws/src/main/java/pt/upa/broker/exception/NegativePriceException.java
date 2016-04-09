package pt.upa.broker.exception;

public final class NegativePriceException extends Exception {
    private static final String DEFAULT_MSG = "Price cannot be lower than 0.";

    public NegativePriceException(String msg) { super(msg); }

    public NegativePriceException() { super(DEFAULT_MSG); }
}
