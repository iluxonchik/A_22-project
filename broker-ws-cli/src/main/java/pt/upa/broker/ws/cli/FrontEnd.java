package pt.upa.broker.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.exception.BrokerClientException;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
import pt.upa.handler.RequestIDHandler;

public class FrontEnd implements BrokerPortType {
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
     * @param uddiURL
     *            UDDI server address
     * @param wsName
     *            name of the broker to connect to
     * @throws BrokerClientException
     */
    public FrontEnd(String uddiURL, String wsName) {
	this.uddiUrl = uddiURL;
	this.wsName = wsName;
	try {
	    // TODO: replace commented out print messages with log messages
	    uddiNaming = new UDDINaming(uddiURL);

	    endpointAddress = uddiNaming.lookup(wsName);

	    if (endpointAddress == null) {
		throw new BrokerClientException(
			"'" + wsName + "' Not Found at " + uddiURL);
	    } else {
		System.out.printf("Found %s%n", endpointAddress);
	    }
	    createStub();
	} catch (JAXRException e) {
	    throw new BrokerClientException("UDDI error: " + e.getMessage(), e);
	}
    }

    /**
     * Instantiate BrokerClient directly from an endpoint address
     *
     * @param endpointAddress
     *            the endpoint address of the
     *            {@link pt.upa.broker.ws.BrokerPortType}
     */
    public FrontEnd(String endpointAddress) {
	uddiNaming = null;
	uddiUrl = null;
	wsName = null;

	this.endpointAddress = endpointAddress;
	createStub();
    }

    private void createStub() {
	service = new BrokerService();
	port = service.getBrokerPort();

	bindingProvider = (BindingProvider) port;
	requestContext = bindingProvider.getRequestContext();
	requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }

    protected void putRequestId(String id) {
	((BindingProvider) port).getRequestContext()
		.put(RequestIDHandler.CONTEXT_REQUEST_ID, id);
    }

    @Override
    public String ping(String name) {
	// TODO: DEMO REMOVE. This is a demo of how requestIDs should be passed,
	// remove the line below
	String reqid = generateId();

	for (int i = 10; i > 0; i--) {
	    try {
		putRequestId(reqid);
		;
		return port.ping(name);
	    } catch (WebServiceException wse) {
		createStub();
	    }
	}
	putRequestId(reqid);
	;
	return port.ping(name);
    }

    @Override
    public String requestTransport(String origin, String destination, int price)
	    throws InvalidPriceFault_Exception,
	    UnavailableTransportFault_Exception,
	    UnavailableTransportPriceFault_Exception,
	    UnknownLocationFault_Exception {
	String reqid = generateId();
	for (int i = 10; i > 0; i--) {
	    try {
		putRequestId(reqid);
		;
		return port.requestTransport(origin, destination, price);
	    } catch (WebServiceException wse) {
		createStub();
	    }
	}
	putRequestId(reqid);
	;
	return port.requestTransport(origin, destination, price);
    }

    @Override
    public TransportView viewTransport(String id)
	    throws UnknownTransportFault_Exception {
	String reqid = generateId();
	for (int i = 10; i > 0; i--) {
	    try {
		putRequestId(reqid);
		return port.viewTransport(id);
	    } catch (WebServiceException wse) {
		createStub();
	    }
	}
	putRequestId(reqid);
	return port.viewTransport(id);
    }

    @Override
    public List<TransportView> listTransports() {
	String reqid = generateId();
	for (int i = 10; i > 0; i--) {
	    try {
		putRequestId(reqid);
		;
		return port.listTransports();
	    } catch (WebServiceException wse) {
		createStub();
	    }
	}
	putRequestId(reqid);
	return port.listTransports();
    }

    @Override
    public void clearTransports() {
	String reqid = generateId();
	for (int i = 10; i > 0; i--) {
	    try {
		putRequestId(reqid);
		port.clearTransports();
		return;
	    } catch (WebServiceException wse) {
		createStub();
	    }
	}
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

    private String generateId() {
	SecureRandom random = new SecureRandom();
	return new BigInteger(130, random).toString(32);
    }

}
