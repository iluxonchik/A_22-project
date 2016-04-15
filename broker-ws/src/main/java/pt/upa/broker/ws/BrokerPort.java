package pt.upa.broker.ws;

import pt.upa.broker.domain.Broker;
import pt.upa.broker.domain.BrokerTransportView;
import pt.upa.shared.Region;
import pt.upa.transporter.ws.cli.TransporterClient;

import javax.jws.WebService;

import java.util.ArrayList;
import java.util.List;


@WebService(
        endpointInterface="pt.upa.broker.ws.BrokerPort",
        wsdlLocation = "broker.1_0.wsdl",
        name = "Broker",
        portName = "TransporterPort",
        targetNamespace="http://ws.broker.upa.pt/",
        serviceName = "BrokerService"
)
public class BrokerPort implements BrokerPortType {
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
        return "Hello " + name + " !";
    }

    @Override
    public String requestTransport(String origin, String destination, int price) throws InvalidPriceFault_Exception,
            UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception,
            UnknownLocationFault_Exception {

        checkArguments(origin, destination, price);

        BrokerTransportView tw = broker.getCheapestTransporter(origin, destination, price);
        if(tw == null) {
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
            if(unknownLocationFault == null) {
                unknownLocationFault = new UnknownLocationFault();
                unknownLocationFault.setLocation(origin);
            }
            throw new UnknownLocationFault_Exception(origin + " is not a known origin region", unknownLocationFault);
        }

        if (!Region.isKnownRegion(destination)) {
            if(unknownLocationFault == null) {
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

}
