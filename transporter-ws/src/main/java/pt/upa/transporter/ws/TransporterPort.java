package pt.upa.transporter.ws;

import pt.upa.shared.Region;
import pt.upa.transporter.domain.TransporterJob;

import javax.jws.WebService;
import java.util.*;

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
    private static BadLocationFault badLocationFault; // to avoid creating multiple instances; lazy instantiation
    private static BadPriceFault badPriceFault; // to avoid creating multiple instances; lazy instantiation
    private static Random rand = new Random();

    private static final int PRICE_UPPER_LIM = 100;
    private static final int PRICE_LOWER_LIM = 10;
    private static final String DEFAULT_NAME = "UpaTransporter1";

    private HashMap<String, TransporterJob> jobs = new HashMap<>();

    public TransporterPort() {
        // required ctor without arguments
        name = DEFAULT_NAME;
    }

	public TransporterPort(String name) {
        this.name = name;
	}

	@Override
	public String ping(String name) {
		return "Hello " + name + "!";
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
        checkRequestJobParams(origin, destination, price);
        int offerPrice;
        if (price > PRICE_UPPER_LIM) {
            return null;
        } else if (price < 10) {
            offerPrice = price - 1 - rand.nextInt(PRICE_LOWER_LIM);
        } else {
            // price between PRICE_LOWER_LIM and PRICE_UPPER_LIM
            offerPrice = computePrice(price);
        }

        TransporterJob job = new TransporterJob(name, origin, destination, offerPrice);
        addJob(job);
		return job;
	}

    private int computePrice(int price) {
        if ((Region.isEvenTransporter(name) && price % 2 == 0) || (!Region.isEvenTransporter(name) && price % 2 != 0)) {
            // transporter number and price have the same parity
            return price - 1 - rand.nextInt(price);
        } else {
            // transporter and price have distinct parities
            // NOTE: this can lead to pretty large price offers...
            return price + 1 + rand.nextInt(Integer.MAX_VALUE - price);
        }
    }


    /**
     * Checks the parameters of {@link TransporterPort#requestJob(String, String, int)}. Throws all the necessary
     * exceptions.
     *
     * @throws BadLocationFault_Exception
     * @throws BadPriceFault_Exception
     */
    private void checkRequestJobParams(String origin, String destination, int price)
            throws BadLocationFault_Exception, BadPriceFault_Exception {
        if (!Region.isKnownByTransporter(name, origin)) {
            if (badLocationFault == null) {
                badLocationFault = new BadLocationFault();
            }
            badLocationFault.setLocation(origin);
            throw new BadLocationFault_Exception("Unknown origin '" + origin + "'", badLocationFault);
        }

        if (!Region.isKnownByTransporter(name, destination)) {
            if (badLocationFault == null) {
                badLocationFault = new BadLocationFault();
            }
            badLocationFault.setLocation(destination);

            throw new BadLocationFault_Exception("Unknown destination '" + destination + "'", badLocationFault);
        }

        if (origin == destination) {
            if (badLocationFault == null) {
                badLocationFault = new BadLocationFault();
            }
            badLocationFault.setLocation(destination);
            throw new BadLocationFault_Exception("Destination cannot be the same as origin", badLocationFault);
        }

        if (price < 0) {
            if (badLocationFault == null) {
                badPriceFault = new BadPriceFault();
            }
            badPriceFault.setPrice(price);
            throw new BadPriceFault_Exception("'" + price + "' is an invalid value for price. It must be >= 0",
                    badPriceFault);
        }

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

    private void addJob(TransporterJob job) {
        jobs.put(job.getJobIdentifier(), job);
    }

	// TODO

}
