package pt.upa.ca.ws.it;

import static org.junit.Assert.*;

import org.junit.Test;
import pt.upa.ca.ws.CertificateReadException_Exception;
import pt.upa.shared.domain.CertificateHelper;

import java.security.cert.Certificate;

public class GetCertificateIT extends AbstractCAIT {
    private static final String DEFAULT_BASE_KEY_DIR = "../keys/";
    private static final String DIR_SEPARATOR = "/";
    private static final String CERT_EXT = ".cer";


    /**
     * Ask request a certificate and make sure the correct one received.
     * @throws Exception
     */
    @Test
    public void getCertificateSuccess() throws Exception {
        final String name = "UpaBroker";
        final Certificate expected = CertificateHelper.readCertificateFile(DEFAULT_BASE_KEY_DIR + name
                + DIR_SEPARATOR + name + CERT_EXT);
        final byte[] certBytes = ca.getCertificate(name);

        assertNotNull("Certificate is null", certBytes);

        final Certificate receivedCert = CertificateHelper.readCertificateFromByteArray(certBytes);
        assertEquals("Certificates do not match", expected, receivedCert);
    }


    /**
     * Tries to read a non-existing certificate.
     * @throws CertificateReadException_Exception
     */
    @Test(expected = CertificateReadException_Exception.class)
    public void getCertificateFail() throws CertificateReadException_Exception {
        ca.getCertificate("Dr.Dre");
    }


}
