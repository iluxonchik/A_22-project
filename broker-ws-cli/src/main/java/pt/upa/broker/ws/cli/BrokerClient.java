package pt.upa.broker.ws.cli;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;


import java.util.List;


@WebService(
        endpointInterface="pt.upa.broker.ws.BrokerPort",
        wsdlLocation = "broker.1_0.wsdl",
        name = "Broker",
        portName = "BrokerPort",
        targetNamespace="http://ws.broker.upa.pt/",
        serviceName = "BrokerService"
)
public class BrokerClient implements BrokerPortType {
	
    public BrokerClient(String uddiUrl, String wsName, String wsUrl) throws JAXRException {
    	UDDINaming uddiNaming = new UDDINaming(uddiUrl);
    	/* TODO */
    }

	@Override
	public String ping(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TransportView> listTransports() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearTransports() {
		// TODO Auto-generated method stub
		
	}


}
