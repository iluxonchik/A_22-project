package pt.upa.ca.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

// classes generated from WSDL
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.exception.CAClientException;
import pt.upa.ca.ws.*;

public class CAClient implements CAPortType {
    private final UDDINaming uddiNaming;
    private final String endpointAddr;
    private final String uddiUrl;
    private final String wsName;
    private CAPortService service;
    private CAPortType port;
    private BindingProvider bindingProvider;
    private Map<String, Object> requestContext;

    private static final String DEFAULT_UDDI_URL = "http://localhost:9090";
    private static final String DEFAULT_WS_NAME = "CA";


    public CAClient() { this(DEFAULT_UDDI_URL, DEFAULT_WS_NAME); }
    public CAClient(String uddiUrl, String wsName) {
        this.uddiUrl = uddiUrl;
        this.wsName = wsName;

        try {
            uddiNaming = new UDDINaming(uddiUrl);
            endpointAddr = uddiNaming.lookup(wsName);

            if (endpointAddr == null) {
                throw new CAClientException("\"" + wsName + "\" not found at " + uddiUrl);
            } else {
                System.out.println("Found " + endpointAddr);
            }
            createStub();
        } catch (JAXRException e) {
            throw new CAClientException("UDDI error: " + e.getMessage());
        }
    }

    public void createStub() {
        service = new CAPortService();
        port = service.getCAPortPort();

        bindingProvider = (BindingProvider) port;
        requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddr);
    }


    @Override
    public byte[] getCertificate(String msg) throws CertificateReadException_Exception {
        return port.getCertificate(msg);
    }

    @Override
    public String ping(String name) {
        return port.ping(name);
    }
}
