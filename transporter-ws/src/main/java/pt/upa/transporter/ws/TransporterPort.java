package pt.upa.transporter.ws;

import javax.jws.WebService;
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

	@Override
	public String ping(String name) {
		// TODO Auto-generated method stub
		return "Hello-Test";
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		// TODO Auto-generated method stub
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
