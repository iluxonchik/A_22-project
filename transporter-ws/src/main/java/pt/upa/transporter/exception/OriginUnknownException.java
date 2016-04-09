package pt.upa.transporter.exception;

public final class OriginUnknownException extends Exception {
    private static final String DEFAULT_MSG = "The provided origin is unknown.";

    public OriginUnknownException(String msg) { super(msg);}

    public OriginUnknownException() { super(DEFAULT_MSG); }
}
