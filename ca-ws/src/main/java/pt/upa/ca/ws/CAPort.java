package pt.upa.ca.ws;

import pt.upa.ca.domain.CA;
import pt.upa.ca.exception.CertificateReadException;

import javax.jws.WebService;

@WebService(endpointInterface = "pt.upa.ca.ws.CAPortType")
public class CAPort implements CAPortType {
    private final CA ca;

    public CAPort() { ca = new CA(); }

	public CAPort(String baseDir) { ca = new CA(baseDir); }

	@Override
	public byte[] getCertificate(String name) throws CertificateReadException {
        return ca.getCertificateByName(name);
	}

    @Override
    public String ping(String name) {
        return "Hello " + name + "!";
    }

}
