package pt.upa.ca.ws;

import javax.jws.WebService;

@WebService(endpointInterface = "pt.upa.ca.ws.CAPortType")
public class CAPort implements CAPortType {

	public String ping(String name) {
		return "Hello " + name + "!";
	}

}
