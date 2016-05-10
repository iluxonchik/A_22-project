package pt.upa.ca.ws;

import pt.upa.ca.domain.UpaCertificate;
import pt.upa.ca.exception.CertificateNotFoundException;

import javax.jws.WebService;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.io.IOException;

@WebService
public interface CAPortType {

	String ping(String name);

    /**
     * Returns a {@link Certificate} with the specified name.

     The actual file <i>name</i> of the certificate will be <i>name</i>.cer.
     * @param name the name of the certificate
     * @return {@link Certificate} with the specified name
     */
	UpaCertificate getCertificate(String name) throws CertificateNotFoundException, IOException, CertificateException;
}
