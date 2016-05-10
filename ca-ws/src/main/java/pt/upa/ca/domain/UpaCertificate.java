package pt.upa.ca.domain;

import pt.upa.ca.exception.CertificateNotPresentException;

import java.security.cert.Certificate;

/**
 * A wrapper around {@link java.security.cert.Certificate}, might contain a Certificate or not. Used because
 * a Web Method is required to have a public no-arg constructor.
 *
 * Clients should call {@link UpaCertificate#isPresent()} before calling {@link UpaCertificate#getCertificate()}
 */
public final class UpaCertificate {

    private Certificate certificate;

    public UpaCertificate() { certificate = null; }
    public UpaCertificate(Certificate cert) { certificate = cert; }

    public boolean isPresent() {
        return certificate != null;
    }

    public Certificate getCertificate() {
        if (certificate == null) {
            throw new CertificateNotPresentException();
        }
        return certificate;
    }
}
