package pt.upa.transporter;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

import javax.xml.ws.BindingProvider;
import java.util.Map;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");

		if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", TransporterClientApplication.class.getName());
            return;
        }

        String uddiURL = args[0];
        String name = args[1];

        System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

        System.out.printf("Searching for '%s'%n", name);
        String endpointAddress = uddiNaming.lookup(name);

        if (endpointAddress == null) {
            System.out.println("'" + name + "' Not Found at " + uddiURL);
            return;
        } else {
            System.out.printf("Found %s%n", endpointAddress);
        }

        System.out.println("Generating stub...");
        TransporterService service = new TransporterService();
        TransporterPortType port = service.getTransporterPort();

        System.out.println("Setting endpoint address...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

        System.out.println("Remote call...");
        String result = port.ping("client");
        System.out.println(result);
	}
}
