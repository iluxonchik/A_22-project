package pt.upa.broker.ws;

import javax.xml.registry.JAXRException;

public class SlaveBrokerPort extends BrokerPort {

	protected BrokerPortType master;
	protected String masterURL;
	
    public SlaveBrokerPort(String uddiUrl, String wsName, String slaveURL, String masterURL) throws JAXRException {
    	super(uddiUrl, wsName, slaveURL);
    	this.masterURL = masterURL;
    	master = getRemoteBroker(masterURL);
    }

    // TODO: ping master and replace it on failure

}
