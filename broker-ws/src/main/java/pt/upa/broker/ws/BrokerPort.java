package pt.upa.broker.ws;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.domain.Broker;
import pt.upa.broker.domain.BrokerTransportView;
import pt.upa.handler.RequestIDHandler;
import pt.upa.shared.Region;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceContext;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;


@WebService(
        endpointInterface = "pt.upa.broker.ws.BrokerPortType",
        wsdlLocation = "broker.2_0.wsdl",
        name = "Broker",
        portName = "BrokerPort",
        targetNamespace = "http://ws.broker.upa.pt/",
        serviceName = "BrokerService"
)
@HandlerChain(file = "/broker_handler-chain.xml")
public class BrokerPort implements BrokerPortType {
    @Resource
    private WebServiceContext webServiceContext;

    private static UnknownLocationFault unknownLocationFault; // to avoid creating multiple instances; lazy instantiation
    private static InvalidPriceFault invalidPriceFault; // to avoid creating multiple instances; lazy instantiation
    private static UnknownTransportFault unknownTransportFault; // to avoid creating multiple instances; lazy instantiation
    private final Broker broker;

    private final String uddiUrl, wsName, wsUrl;

    public BrokerPort() {
        /* Required default constructor */
        this(null, null, null);
    }

    public BrokerPort(String uddiUrl, String wsName, String wsUrl) {
        this.broker = new Broker(uddiUrl);
        this.uddiUrl = uddiUrl;
        this.wsName = wsName;
        this.wsUrl = wsUrl;
    }

    @Override
    public String ping(String name) {
        // TODO: DEMO REMOVE. This is a demo of how requestIDs should be read back from the Handler, remove the two lines below
        String headerVal = (String)webServiceContext.getMessageContext().get(RequestIDHandler.CONTEXT_REQUEST_ID);
        System.out.println("---TEST---\nReceived value: " + headerVal + "\n---TEST---");

        return "Hello " + name + " !";
    }

    @Override
    public String requestTransport(String origin, String destination, int price) throws InvalidPriceFault_Exception,
            UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception,
            UnknownLocationFault_Exception {

        checkArguments(origin, destination, price);

        BrokerTransportView tw = broker.getCheapestTransporter(origin, destination, price);
        if (tw == null) {
            // no offer for goal price
            if (invalidPriceFault == null) {
                invalidPriceFault = new InvalidPriceFault();
            }
            invalidPriceFault.setPrice(price);
            throw new InvalidPriceFault_Exception("No offers for selected price range", invalidPriceFault);
        }

        return tw.getId(); // offer identifier on Broker's side. NOTE: different from Transporter's identifier
    }

    private void checkArguments(String origin, String destination, int price) throws UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        if (!Region.isKnownRegion(origin)) {
            if (unknownLocationFault == null) {
                unknownLocationFault = new UnknownLocationFault();
                unknownLocationFault.setLocation(origin);
            }
            throw new UnknownLocationFault_Exception(origin + " is not a known origin region", unknownLocationFault);
        }

        if (!Region.isKnownRegion(destination)) {
            if (unknownLocationFault == null) {
                unknownLocationFault = new UnknownLocationFault();
                unknownLocationFault.setLocation(destination);
            }
            throw new UnknownLocationFault_Exception(destination + " is not a known destination region",
                    unknownLocationFault);
        }

        if (origin == destination) {
            if (unknownLocationFault == null) {
                unknownLocationFault = new UnknownLocationFault();
            }
            unknownLocationFault.setLocation(destination);
            throw new UnknownLocationFault_Exception("Destination cannot be the same as origin",
                    unknownLocationFault);
        }

        if (price < 0) {
            if (invalidPriceFault == null) {
                invalidPriceFault = new InvalidPriceFault();
            }
            invalidPriceFault.setPrice(price);
            throw new InvalidPriceFault_Exception("'" + price + "' is an invalid value for price. It must be >= 0",
                    invalidPriceFault);
        }
    }

    @Override
    public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
        // NOTE: BrokerTransportView overrides getState() and contacts Transporter if it's necessary
        TransportView tw = broker.getJobById(id);
        if (tw == null) {
            if (unknownTransportFault == null) {
                unknownTransportFault = new UnknownTransportFault();
            }
            unknownTransportFault.setId(id);
            throw new UnknownTransportFault_Exception("Unknown tranport id: " + id, unknownTransportFault);
        } else {
            return tw;
        }
    }

    @Override
    public List<TransportView> listTransports() {
        return broker.getJobList();
    }

    @Override
    public void clearTransports() {
        broker.clearTransports();
    }

	@Override
	public String updateState(boolean clearJobs, int counter, BrokerTVUpdateType brokerTVU) {
		if(clearJobs) {
			clearTransports();
			return "clear";
		} else {
			broker.updateState(brokerTVU, counter);
			return "update";
		}
	}

	protected void sendUpdate(BrokerPortType slave, boolean clearJobs, String id) {
		int counter = 0;
		BrokerTVUpdateType brokerTVU = null;
		if(!clearJobs) {
			counter = broker.getCounter();
			brokerTVU = broker.getBTVUpdate(id);
		}
		slave.updateState(clearJobs, counter, brokerTVU);
	}
	
	protected BrokerPortType getRemoteBroker(String url) throws JAXRException {
		UDDINaming uddiNaming = new UDDINaming(uddiUrl);
	    String endpointAddress = uddiNaming.lookup(wsName);
		BrokerService service = new BrokerService();
		BrokerPortType port = service.getBrokerPort();
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		return port;
	}
	
}
