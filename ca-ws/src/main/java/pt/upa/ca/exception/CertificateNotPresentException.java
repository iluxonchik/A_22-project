package pt.upa.ca.exception;


public class CertificateNotPresentException extends RuntimeException {
    private static final String MSG = "Certificate is not present";

    public CertificateNotPresentException() {
        super(MSG);
    }
}
