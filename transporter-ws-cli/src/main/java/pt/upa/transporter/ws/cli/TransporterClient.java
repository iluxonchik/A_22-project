package pt.upa.transporter.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

import javax.xml.ws.BindingProvider;
import java.util.Map;

/**
 * Client instance tied to a specific uddiURL and transporter name
 */
public final class TransporterClient {

    private final UDDINaming uddiNaming;
    private final String endpointAddress;
    private final TransporterService service;
    private final TransporterPortType port;
    private final BindingProvider bindingProvider;
    private final Map<String, Object> requestContext;

    /**
     *
     * @param uddiURL UDDI server address
     * @param name name of the transporter to connect to
     * @throws Exception
     */
    public TransporterClient(String uddiURL, String name) throws Exception {
            //TODO: replace commented out print messages with log messages
            //System.out.printf("Contacting UDDI at %s%n", uddiURL);
            uddiNaming = new UDDINaming(uddiURL);

            //System.out.printf("Searching for '%s'%n", name);
            endpointAddress = uddiNaming.lookup(name);

            if (endpointAddress == null) {
                // TODO: add proper exception
                throw new Exception("'" + name + "' Not Found at " + uddiURL);
            } else {
                System.out.printf("Found %s%n", endpointAddress);
            }

            //System.out.println("Generating stub...");
            service = new TransporterService();
            port = service.getTransporterPort();

            //System.out.println("Setting endpoint address...");
            bindingProvider = (BindingProvider) port;
            requestContext = bindingProvider.getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }

    public void ping(String msg) {
        System.out.println(port.ping(msg));
    }

    public UDDINaming getUddiNaming() {
        return uddiNaming;
    }

    public String getEndpointAddress() {
        return endpointAddress;
    }

    public TransporterService getService() {
        return service;
    }

    public TransporterPortType getPort() {
        return port;
    }

    public BindingProvider getBindingProvider() {
        return bindingProvider;
    }

    public Map<String, Object> getRequestContext() {
        return requestContext;
    }
}
