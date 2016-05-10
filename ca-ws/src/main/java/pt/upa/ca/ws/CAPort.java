package pt.upa.ca.ws;

import pt.upa.ca.domain.CA;
import pt.upa.ca.domain.UpaCertificate;
import pt.upa.ca.exception.CertificateNotFoundException;

import javax.jws.WebService;
import java.io.IOException;
import java.security.cert.CertificateException;

@WebService(endpointInterface = "pt.upa.ca.ws.CAPortType")
public class CAPort implements CAPortType {
    private final CA ca;

    public CAPort() { ca = new CA(); }

	public CAPort(String baseDir) { ca = new CA(baseDir); }

	@Override
	public UpaCertificate getCertificate(String name) throws CertificateNotFoundException, IOException,
            CertificateException {
        return ca.getCertificateByName(name);
	}

    @Override
    public String ping(String name) {
        return "Hello " + name + "!";
    }

}
