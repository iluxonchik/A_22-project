package pt.upa.broker.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.exception.BrokerClientException;
import pt.upa.broker.ws.*;
import pt.upa.handler.RequestIDHandler;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import java.util.List;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;


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
            uddiNaming = new UDDINaming(uddiURL);

            endpointAddress = uddiNaming.lookup(wsName);

            if (endpointAddress == null) {
                throw new BrokerClientException("'" + wsName + "' Not Found at " + uddiURL);
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
     * @param endpointAddress the endpoint address of the {@link pt.upa.broker.ws.BrokerPortType}
     */
    public BrokerClient(String endpointAddress) {
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

    @Override
    public String ping(String name) {
        // TODO: DEMO REMOVE. This is a demo of how requestIDs should be passed, remove the line below
        ((BindingProvider)port).getRequestContext().put(RequestIDHandler.CONTEXT_REQUEST_ID, "Dr.Dre");

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
