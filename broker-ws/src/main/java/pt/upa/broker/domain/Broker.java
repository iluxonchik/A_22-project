package pt.upa.broker.domain;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.exception.BrokerException;
import pt.upa.broker.ws.TransportView;
import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.registry.JAXRException;
import java.util.Collection;
import java.util.HashMap;

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

    public BrokerTransportView getCheapestTransporter(String origin, String destination, int maxPrice) {
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
                } catch (BadLocationFault_Exception e ){
                    // transporter doesn't recognize an origin or a destination, no biggie, just ignore it and proceed to the
                    // next one
                } catch (BadPriceFault_Exception e) {
                    // BrokerPort already does argument checks, so this should never happen
                    throw new BrokerException("Price error: " + e.getMessage());
                }
            }

            chosenJob = tw.scheduleJob();
            return chosenJob;
        } catch (JAXRException e) {
            throw new BrokerException("Could not get endpoint list: " + e.getMessage());
        } catch (BadJobFault_Exception e) {
            // something went wrong during the rejection/acceptance of the offer
            throw new BrokerException("Error: " + e.getMessage());
        }
    }

    private Collection<String> getTransportersList() throws JAXRException {
        UDDINaming naming = new UDDINaming(uddiUrl);
        return naming.list(TRANSPORTER_WLDCRT);
    }

    private String getUID() {
        counter++;
        return  "BROKER_" + counter;

    }
    public TransportView getJobById(String id) {
        return jobs.get(id);
    }

}
