package pt.upa.broker.ws;


import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.registry.JAXRException;


@WebService(
        endpointInterface = "pt.upa.broker.ws.BrokerPortType",
        wsdlLocation = "broker.2_0.wsdl",
        name = "Broker",
        portName = "BrokerPort",
        targetNamespace = "http://ws.broker.upa.pt/",
        serviceName = "BrokerService"
)
@HandlerChain(file = "/broker_handler-chain.xml")
public class MasterBrokerPort extends BrokerPort {
	protected BrokerPortType slave;
	protected String slaveURL;
    public MasterBrokerPort(String uddiUrl, String wsName, String masterURL, String slaveURL) throws JAXRException {
    	super(uddiUrl, wsName, masterURL);
    	this.slaveURL = slaveURL;
    	slave = getRemoteBroker(slaveURL);
    }

    @Override
    public String ping(String name) {
    	// Broker state is not changed here
        return super.ping(name);
    }

    @Override
    public String requestTransport(String origin, String destination, int price) throws InvalidPriceFault_Exception,
            UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception,
            UnknownLocationFault_Exception {
    	String response = super.requestTransport(origin, destination, price);
    	// TODO: cache response and send it to slave
    	sendUpdate(slave, false, response); // response is the id of the job
    	return response;
    }

    @Override
    public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
        // NOTE: BrokerTransportView overrides getState() and contacts Transporter if it's necessary
        TransportView tw = super.viewTransport(id);
        // TODO: cache response cache response and send it to slave
        sendUpdate(slave, false, id); // the TransportView might be modified when getting its state from the Transporter
        return tw;
    }

    @Override
    public List<TransportView> listTransports() {
    	// Broker state is not changed here
        return super.listTransports();
    }

    @Override
    public void clearTransports() {
    	sendUpdate(slave, true, null);
        super.clearTransports();
    }

	
}
