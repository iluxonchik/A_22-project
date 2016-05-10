package pt.upa.ca.ws;

import pt.upa.ca.domain.CA;

import javax.jws.WebService;
import javax.security.cert.Certificate;

@WebService(endpointInterface = "pt.upa.ca.ws.CAPortType")
public class CAPort implements CAPortType {
    private final CA ca;

    public CAPort() { ca = new CA(); }

	public CAPort(String baseDir) { ca = new CA(baseDir); }

	@Override
	public Certificate getCertificate(String name) {
		// TODO
        return null;
	}

    @Override
    public String ping(String name) {
        return "Hello " + name + "!";
    }

}
