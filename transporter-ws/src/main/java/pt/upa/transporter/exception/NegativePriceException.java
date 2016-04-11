package pt.upa.transporter.exception;

public final class NegativePriceException extends TransporterException {
    private static final String DEFAULT_MSG = "Price cannot be lower than 0.";

    public NegativePriceException(String msg) { super(msg); }

    public NegativePriceException() { super(DEFAULT_MSG); }
}
