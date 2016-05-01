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

    private UnavailableTransportFault unavailableTransportFault;
    private UnavailableTransportPriceFault unavailableTransportPriceFault;

    private final int maxPrice;
    private int lowestPrice = Integer.MAX_VALUE; // optional, can use JobView's price instead, but this makes it clearer
    private JobView bestJob;
    private TransporterClient client;
    // the corresponding job id on the transporter's side (this.id corresponds to transporters transporterJobId)
    // used internally to communicate with the transporter
    private String transporterJobId;

    public BrokerTransportView(String origin, String destination, int maxPrice, String UID) {
        this.origin = origin;
        this.destination = destination;
        this.maxPrice = maxPrice;
        this.id = UID;
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
        } else if (!(lowestPrice < maxPrice)) {
            // at least one offer from the transporters, but none of them meet the price requirements
            failJob();
            client.decideJob(bestJob.getJobIdentifier(), false);
            initUnavailableTransportPriceFault();
            this.unavailableTransportPriceFault.setBestPriceFound(lowestPrice);
            throw new UnavailableTransportPriceFault_Exception("No avaialable transports for the specified price",
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
                // this should never happen
                // TODO: consider returning null/throwing an exception
                return TransportStateView.BOOKED;
        }
    }

    private void failJob() {
        this.state = TransportStateView.FAILED;
    }

    public int getLowestPrice() {
        return lowestPrice;
    }

    public JobView getBestJob() {
        return bestJob;
    }

    public String getTransporterJobId() {
        return this.transporterJobId;
    }

    public TransporterClient getClient() {
        return client;
    }

    public int getMaxPrice() {
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
