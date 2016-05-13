package pt.upa.broker.ws;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.registry.JAXRException;


@WebService(
        endpointInterface = "pt.upa.broker.ws.BrokerPortType",
        wsdlLocation = "broker.2_0.wsdl",
        name = "BrokerSlave",
        portName = "BrokerPort",
        targetNamespace = "http://ws.broker.upa.pt/",
        serviceName = "BrokerService"
)
@HandlerChain(file = "/broker_handler-chain.xml")
public class SlaveBrokerPort extends BrokerPort {

	protected BrokerPortType master;
	protected String masterURL;
	
    public SlaveBrokerPort(String uddiUrl, String wsName, String slaveURL, String masterURL) throws JAXRException {
    	super(uddiUrl, wsName, slaveURL);
    	this.masterURL = masterURL;
    	//master = getRemoteBroker(masterURL); // XXX NOT USED: master will ping slave. slave does not need to contact master
    }

    // TODO: ping master and replace it on failure

}
