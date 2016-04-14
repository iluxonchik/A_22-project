package pt.upa.broker.ws;

import pt.upa.shared.Region;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;


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
    private static final String TRANSPORTER_WLDCRT = "UpaTransporter%";
    
    private final String uddiUrl, wsName, wsUrl;

    public BrokerPort() { 
    	/* Required default constructor */
    	this(null, null, null); 
    }
    
    public BrokerPort(String uddiUrl, String wsName, String wsUrl) {
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
        // TODO: check if there is an available transport from origin to destination, contact Transporters for that
        

        return null; // TODO: return transport identifier
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
    
  private Collection<String>  getTransportersList() throws JAXRException {
    	UDDINaming naming = new UDDINaming(uddiUrl);
    	return naming.list(TRANSPORTER_WLDCRT);
  }
  
 

    @Override
    public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
        return null;
    }

    @Override
    public List<TransportView> listTransports() {
        return null;
    }

    @Override
    public void clearTransports() {

    }

}
