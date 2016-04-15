package pt.upa.broker.ws.cli;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;
import pt.upa.broker.exception.BrokerClientException;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;


public class BrokerClient implements BrokerPortType {
    private final UDDINaming uddiNaming;
    private final String endpointAddress; // A.K.A. wsUrl
    private final String uddiUrl;
    private final String wsName;
    private BrokerService service;
    private BrokerPortType port;
    private BindingProvider bindingProvider;
    private Map<String, Object> requestContext;
    
    /**
     * Instantiate a BrokerClient from UDDI url and wsName.
     *
     * @param uddiURL UDDI server address
     * @param wsName  name of the broker to connect to
     * @throws BrokerClientException
     */
    public BrokerClient(String uddiURL, String wsName) {
        this.uddiUrl = uddiURL;
        this.wsName = wsName;
        try {
            //TODO: replace commented out print messages with log messages
            //System.out.printf("Contacting UDDI at %s%n", uddiURL);
            uddiNaming = new UDDINaming(uddiURL);

            //System.out.printf("Searching for '%s'%n", name);
            endpointAddress = uddiNaming.lookup(wsName);

            if (endpointAddress == null) {
                throw new BrokerClientException("'" + wsName + "' Not Found at " + uddiURL);
            } else {
                System.out.printf("Found %s%n", endpointAddress);
            }
            createStub();
        } catch (JAXRException e) {
            throw new BrokerClientException("UDDI error: " + e.getMessage());
        }
    }
    
    private void createStub() {
        //System.out.println("Generating stub...");
        service = new BrokerService();
        port = service.getBrokerPort();

        //System.out.println("Setting endpoint address...");
        bindingProvider = (BindingProvider) port;
        requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }

    /**
     * Instantiate BrokerClient directly from an endpoint address
     *
     * @param endpointAddress the endpoint address of the {@link pt.upa.Broker.ws.BrokerPortType}
     */
    public BrokerClient(String endpointAddress) {
        uddiNaming = null;
        uddiUrl = null;
        wsName = null;

        this.endpointAddress = endpointAddress;
        createStub();
    }

	@Override
	public String ping(String name) {
        return port.ping(name);
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		return port.requestTransport(origin, destination, price);
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		return port.viewTransport(id);
	}

	@Override
	public List<TransportView> listTransports() {
		return port.listTransports();
	}

	@Override
	public void clearTransports() {
		port.clearTransports();
	}

    public UDDINaming getUddiNaming() {
        return uddiNaming;
    }

    public String getEndpointAddress() {
        return endpointAddress;
    }

    public BrokerService getService() {
        return service;
    }

    public BrokerPortType getPort() {
        return port;
    }

    public BindingProvider getBindingProvider() {
        return bindingProvider;
    }

    public Map<String, Object> getRequestContext() {
        return requestContext;
    }


    public String getUddiUrl() {
        return uddiUrl;
    }

    public String getWsName() {
        return wsName;
    }


}
