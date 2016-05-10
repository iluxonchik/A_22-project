package pt.upa.ca.ws;

import javax.jws.WebService;

@WebService
public interface CAPortType {

	String ping(String name);


}
