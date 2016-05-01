package pt.upa.broker.domain;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.exception.BrokerException;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.registry.JAXRException;
import java.util.*;

/**
 * A Broker tied to a specific URL of UDDI
 */
public final class Broker {
    private static final String TRANSPORTER_WLDCRT = "UpaTransporter%";

    private static int counter = 0;
    private final String uddiUrl;
    private TransporterClient client;
    private HashMap<String, BrokerTransportView> jobs = new HashMap<>();

    public Broker(String uddiUrl) {
        this.uddiUrl = uddiUrl;
    }

    public BrokerTransportView getCheapestTransporter(String origin, String destination, int maxPrice)
            throws UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception {
        JobView jw;
        BrokerTransportView chosenJob;
        BrokerTransportView tw = new BrokerTransportView(origin, destination, maxPrice, getUID());
        jobs.put(tw.getId(), tw);
        try {
            Collection<String> endpoints = getTransportersList();

            // iterate through the list of endpoints and select the lowest offer below maxPrice (if such exists)
            for (String endpoint : endpoints) {
                client = new TransporterClient(endpoint);
                try {
                    jw = client.requestJob(origin, destination, maxPrice);
                    tw.processJobOffer(jw, client);
                } catch (BadLocationFault_Exception e) {
                    // transporter doesn't recognize an origin or a destination, no biggie, just ignore it and proceed to the
                    // next one
                } catch (BadPriceFault_Exception e) {
                    // BrokerPort already does argument checks, so this should never happen
                    throw new BrokerException("Price error: " + e.getMessage());
                }
            }

            try {
                chosenJob = tw.scheduleJob();
            } catch (UnavailableTransportFault_Exception e) {
                throw e;
            }

            return chosenJob;
        } catch (JAXRException e) {
            throw new BrokerException("Could not get endpoint list: " + e.getMessage());
        } catch (BadJobFault_Exception e) {
            // something went wrong during the rejection/acceptance of the offer
            e.printStackTrace();
            throw new BrokerException("Error: " + e.getMessage());
        }
    }

    private Collection<String> getTransportersList() throws JAXRException {
        UDDINaming naming = new UDDINaming(uddiUrl);
        return naming.list(TRANSPORTER_WLDCRT);
    }

    private String getUID() {
        counter++;
        return "BROKER_" + counter;

    }

    public TransportView getJobById(String id) {
        BrokerTransportView btw = jobs.get(id);
        if (btw != null)
            btw.setState(btw.getState());
        return btw;
    }

    public List<TransportView> getJobList() {
        return new ArrayList<>(jobs.values());
    }

    public Set<TransporterClient> getTransporterClients() {
        // TODO: refactor to use Java 8 Streams
        Set<TransporterClient> clients = new LinkedHashSet<>();
        for (BrokerTransportView btw : jobs.values()) {
            clients.add(btw.getClient());
        }
        return clients;
    }

    public void clearTransports() {
        getTransporterClients()
                .stream()
                .filter(c -> c != null)
                .forEach(c -> c.clearJobs());
        jobs.clear();
    }
}
