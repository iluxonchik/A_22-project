package pt.upa.transporter.ws;

import pt.upa.handler.AuthenticationHandler;
import pt.upa.shared.Region;
import pt.upa.shared.domain.CertificateHelper;
import pt.upa.transporter.domain.TransporterJob;
import pt.upa.transporter.exception.InvalidJobStateTransitionException;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECField;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@WebService(
        endpointInterface = "pt.upa.transporter.ws.TransporterPortType",
        wsdlLocation = "transporter.1_0.wsdl",
        name = "Transporter",
        portName = "TransporterPort",
        targetNamespace = "http://ws.transporter.upa.pt/",
        serviceName = "TransporterService"
)
@HandlerChain(file = "/transporter_handler-chain.xml")
public class TransporterPort implements TransporterPortType {
    @Resource
    private WebServiceContext webServiceContext;

    private static final Random rand = new Random();
    private static final Timer timer = new Timer();
    private static final int PRICE_UPPER_LIM = 100;
    private static final int PRICE_LOWER_LIM = 10;
    private static final int MINIMUM_PRICE = 1;
    private static final String DEFAULT_NAME = "UpaTransporter1";
    private static BadLocationFault badLocationFault; // to avoid creating multiple instances; lazy instantiation
    private static BadPriceFault badPriceFault; // to avoid creating multiple instances; lazy instantiation
    private static BadJobFault badJobFault; // to avoid creating multiple instances; lazy instantiation

    public String getName() {
        return name;
    }

    private final String name;
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
        setReqContext();
        return "Hello " + name + "!";
    }

    @Override
    public JobView requestJob(String origin, String destination, int price)
            throws BadLocationFault_Exception, BadPriceFault_Exception {
        setReqContext();
        checkRequestJobParams(origin, destination, price);
        int offerPrice;
        if (price > PRICE_UPPER_LIM) {
            return null;
        } else if (price <= 10) {
            offerPrice = price - 1 - rand.nextInt(PRICE_LOWER_LIM);
        } else {
            // price between PRICE_LOWER_LIM and PRICE_UPPER_LIM
            offerPrice = computePrice(price);
        }
        if (offerPrice < MINIMUM_PRICE)
            offerPrice = MINIMUM_PRICE;

        TransporterJob job = new TransporterJob(name, origin, destination, offerPrice);
        addJob(job);
        return job;
    }

    private int computePrice(int price) {
        if ((Region.isEvenTransporter(name) && price % 2 == 0) || (!Region.isEvenTransporter(name) && price % 2 != 0)) {
            // transporter number and price have the same parity
            price = price - 1 - rand.nextInt(price);
        } else {
            // transporter and price have distinct parities
            // NOTE: this can lead to pretty large price offers...
            price = price + 1 + rand.nextInt(Integer.MAX_VALUE - price);
        }
        return (price < MINIMUM_PRICE) ? MINIMUM_PRICE : price;
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
            if (badPriceFault == null) {
                badPriceFault = new BadPriceFault();
            }
            badPriceFault.setPrice(price);
            throw new BadPriceFault_Exception("'" + price + "' is an invalid value for price. It must be >= 0",
                    badPriceFault);
        }

    }

    @Override
    public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
        setReqContext();
        checkJob(id);
        TransporterJob job = jobs.get(id); // null check done in method above
        if (accept) {
            if (job.getJobState() == JobStateView.ACCEPTED) {
                initBadJobFault();
                badJobFault.setId(id);
                throw new BadJobFault_Exception("Cannot ACCEPT the same job twice", badJobFault);
            }
            job.nextJobState();
            startTimer(job);
        } else {
            if (job.getJobState() == JobStateView.REJECTED) {
                initBadJobFault();
                badJobFault.setId(id);
                throw new BadJobFault_Exception("Cannot REJECT the same job twice", badJobFault);
            }

            try {
                job.rejectJob();
            } catch (InvalidJobStateTransitionException e) {
                initBadJobFault();
                badJobFault.setId(id);
                throw new BadJobFault_Exception(e.getMessage(), badJobFault);
            }
        }
        return job;
    }

    private void startTimer(TransporterJob job) {
        timer.schedule(new JobTimer(job), (1 + ThreadLocalRandom.current().nextLong(JobTimer.MAX_TIMER_VAL)) * 1000);
    }

    /**
     * Checks if a job id maps to a valid job.
     *
     * @param id job id
     * @throws BadJobFault_Exception
     */
    private void checkJob(String id) throws BadJobFault_Exception {
        if (!jobs.containsKey(id)) {
            initBadJobFault();
            badJobFault.setId(id);
            throw new BadJobFault_Exception("'" + id + "' is an invalid job id", badJobFault);
        }

        if (jobs.get(id) == null) {
            initBadJobFault();
            badJobFault.setId(id);
            throw new BadJobFault_Exception("'" + id + "' maps to a null job", badJobFault);
        }
    }

    @Override
    public JobView jobStatus(String id) {
        setReqContext();
        // TODO: not the best programming practice... Refractor if there is time
        try {
            checkJob(id);
        } catch (BadJobFault_Exception e) {
            return null;
        }
        return jobs.get(id);
    }

    @Override
    public List<JobView> listJobs() {
        setReqContext();
        return new ArrayList<>(jobs.values());
    }

    @Override
    public void clearJobs() {
        setReqContext();
        jobs.clear();
    }

    private void addJob(TransporterJob job) {
        jobs.put(job.getJobIdentifier(), job);
    }

    private void initBadJobFault() {
        if (this.badJobFault == null) {
            this.badJobFault = new BadJobFault();
        }
    }

    private class JobTimer extends TimerTask {
        public static final int MAX_TIMER_VAL = 5;
        private final TransporterJob job;

        public JobTimer(TransporterJob job) {
            this.job = job;
        }

        @Override
        public void run() {
            job.nextJobState();
            if (!job.isCompleted()) {
                timer.schedule(new JobTimer(job), (1 + ThreadLocalRandom.current().nextLong(MAX_TIMER_VAL)) * 1000);
            }
        }
    }


    private void setReqContext() {
        try {
            webServiceContext.getMessageContext().put(AuthenticationHandler.CONTEXT_SENDER_NAME, getName());
            webServiceContext.getMessageContext().put(AuthenticationHandler.CONTEXT_PRIVATE_KEY,
                    CertificateHelper.getPrivateKey(getName()));
        } catch (Exception e) {
            System.out.println("[FATAL!!!] FAILED to get private key");
            e.printStackTrace();

        }
    }

}
