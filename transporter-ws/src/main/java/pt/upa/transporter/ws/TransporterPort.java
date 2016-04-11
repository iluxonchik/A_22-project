package pt.upa.transporter.ws;

import pt.upa.shared.Region;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.util.List;

@WebService(
        endpointInterface="pt.upa.transporter.ws.TransporterPort",
        wsdlLocation = "transporter.1_0.wsdl",
        name = "Transporter",
        portName = "TransporterPort",
        targetNamespace="http://ws.transporter.upa.pt/",
        serviceName = "TransporterService"
)
public class TransporterPort implements TransporterPortType {
	private final String name;

    public TransporterPort() {
        // required ctor without arguments
        name = null;
    }

	public TransporterPort(String name) {
        this.name = name;
	}

	@Override
	public String ping(String name) {
		// TODO Auto-generated method stub
		return "Hello " + name + "!";
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
        if (!Region.isKnownRegion(origin)) {


        }
		return null;
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobView jobStatus(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobView> listJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearJobs() {
		// TODO Auto-generated method stub
		
	}

	// TODO

}
