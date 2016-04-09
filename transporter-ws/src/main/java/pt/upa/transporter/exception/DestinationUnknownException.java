package pt.upa.transporter.exception;

public final class DestinationUnknownException extends Exception {
    private static final String DEFAULT_MSG = "The provided destination is unknown.";

    public DestinationUnknownException(String msg) { super(msg);}

    public DestinationUnknownException() { super(DEFAULT_MSG); }
}
