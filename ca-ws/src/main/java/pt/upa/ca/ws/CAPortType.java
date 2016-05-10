package pt.upa.ca.ws;

import javax.jws.WebService;
import javax.security.cert.Certificate;

@WebService
public interface CAPortType {

	String ping(String name);

    /**
     * Returns a {@link Certificate} with the specified name.

     The actual file <i>name</i> of the certificate will be <i>name</i>.cer.
     * @param name the name of the certificate
     * @return {@link Certificate} with the specified name
     */
	Certificate getCertificate(String name);
}
