package pt.upa.broker.domain;

import pt.upa.broker.ws.*;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

/**
 * Extends {@link TransportView} with a nicer interface.
 */
public class BrokerTransportView extends TransportView {

    private final int maxPrice;
    private UnavailableTransportFault unavailableTransportFault;
    private UnavailableTransportPriceFault unavailableTransportPriceFault;
	/// no need to sync with slave
    private int lowestPrice = Integer.MAX_VALUE; // optional, can use JobView's price instead, but this makes it clearer
	/// no need to sync with slave
    private JobView bestJob;
    /// can recreate it from String endpoint
    private TransporterClient client;
    // the corresponding job id on the transporter's side (this.id corresponds to transporters transporterJobId)
    // used internally to communicate with the transporter
    private String transporterJobId;

    public BrokerTransportView(BrokerTVUpdateType btvu) {
    	maxPrice = btvu.getMaxPrice();
    	client   = new TransporterClient(btvu.getClientEndpoint());
    	transporterJobId = btvu.getTransporterJobId();
    	TransportView tv = btvu.getTransporterView();
    	
    	// TransportView's attributes
    	this.id=tv.getId();
    	this.origin=tv.getOrigin();
    	this.destination=tv.getDestination();
    	this.price=tv.getPrice();
    	this.transporterCompany=tv.getTransporterCompany();
    	this.state=tv.getState();
    	
        initUnavailableTransportFault();
        initUnavailableTransportPriceFault();
    }
    
    public BrokerTVUpdateType toBTVUpdate() {
    	BrokerTVUpdateType state = new BrokerTVUpdateType();
    	
    	state.setMaxPrice(maxPrice);
    	state.setClientEndpoint(client.getEndpointAddress());
    	state.setTransporterJobId(transporterJobId);

    	// TransportView's attributes
    	TransportView tv = new TransportView();
    	tv.setId(this.id);
    	tv.setOrigin(this.origin);
    	tv.setDestination(this.destination);
    	tv.setPrice(this.price);
    	tv.setTransporterCompany(this.transporterCompany);
    	tv.setState(this.state);
    	state.setTransporterView(tv);
    	
    	return state;
    }
    
    
    public BrokerTransportView(String origin, String destination, int maxPrice, String UID) {
        this.origin = origin;
        this.destination = destination;
        this.maxPrice = maxPrice;
        this.id = UID;
        this.state = TransportStateView.REQUESTED;
    }

    public void processJobOffer(JobView jw, TransporterClient client) throws BadJobFault_Exception {
        if (jw == null) {
            return;
        }
        if (jw.getJobPrice() < lowestPrice) {
            this.state = TransportStateView.BUDGETED; // there is at least one offer, so the state is now BUDGETED
            if (bestJob != null) {
                this.client.decideJob(bestJob.getJobIdentifier(), false); // reject best previous job
            }
            this.lowestPrice = jw.getJobPrice();
            this.client = client; // update client
            bestJob = jw; // TODO: was inside if before
        } else {
            client.decideJob(jw.getJobIdentifier(), false);
        }
    }

    /**
     * Schedules and returns if there is a job offer that meets the criteria and returns that
     * {@link JobView}, otherwise returns null.
     *
     * @return the scheduled {@link JobView} or null if none of the offers met the criteria
     */
    public BrokerTransportView scheduleJob() throws BadJobFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception {
        if (this.state == TransportStateView.REQUESTED) {
            // no offers from transporters
            failJob();
            initUnavailableTransportFault();
            this.unavailableTransportFault.setOrigin(this.origin);
            this.unavailableTransportFault.setDestination(this.destination);
            throw new UnavailableTransportFault_Exception("No available transports found", unavailableTransportFault);
        } else if (!(lowestPrice < maxPrice)) {
            // at least one offer from the transporters, but none of them meet the price requirements
            failJob();
            client.decideJob(bestJob.getJobIdentifier(), false);
            initUnavailableTransportPriceFault();
            this.unavailableTransportPriceFault.setBestPriceFound(lowestPrice);
            throw new UnavailableTransportPriceFault_Exception("No available transports for the specified price",
                    unavailableTransportPriceFault);
        } else {
            // there is an offer lower than the maxPrice, so let's accept it
            this.transporterJobId = bestJob.getJobIdentifier();
            client.decideJob(bestJob.getJobIdentifier(), true);

            this.price = bestJob.getJobPrice();
            this.transporterCompany = bestJob.getCompanyName();
            this.state = TransportStateView.BOOKED;
        }
        return this;
    }

    @Override
    public TransportStateView getState() {
        switch (super.getState()) {
            case REQUESTED:
                return TransportStateView.REQUESTED;
            case BUDGETED:
                return TransportStateView.BUDGETED;
            case FAILED:
                return TransportStateView.FAILED;
            default:
                System.out.print("Contacting transporter for job state...");
                this.state = getJobStateFromTransporter();
                break;
        }
        return this.state;
    }

    private TransportStateView getJobStateFromTransporter() {
        JobStateView jsw = client.jobStatus(this.transporterJobId).getJobState();
        switch (jsw) {
            case HEADING:
                return TransportStateView.HEADING;
            case ONGOING:
                return TransportStateView.ONGOING;
            case COMPLETED:
                return TransportStateView.COMPLETED;
            default:
                return TransportStateView.BOOKED;
        }
    }

    private void failJob() {
        this.state = TransportStateView.FAILED;
    }

    protected int getLowestPrice() {
        return lowestPrice;
    }

    protected JobView getBestJob() {
        return bestJob;
    }

    protected String getTransporterJobId() {
        return this.transporterJobId;
    }

    protected TransporterClient getClient() {
        return client;
    }

    protected int getMaxPrice() {
        return maxPrice;
    }

    private void initUnavailableTransportFault() {
        if (this.unavailableTransportFault == null) {
            unavailableTransportFault = new UnavailableTransportFault();
        }
    }

    private void initUnavailableTransportPriceFault() {
        if (this.unavailableTransportPriceFault == null) {
            unavailableTransportPriceFault = new UnavailableTransportPriceFault();
        }
    }

}
