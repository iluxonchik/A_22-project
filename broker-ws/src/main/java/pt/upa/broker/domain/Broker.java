package pt.upa.broker.domain;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.exception.BrokerException;
import pt.upa.broker.ws.BrokerTVUpdateType;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.handler.AuthenticationHandler;
import pt.upa.shared.domain.CertificateHelper;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.registry.JAXRException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * A Broker tied to a specific URL of UDDI
 */
public final class Broker {
	
    private static final String TRANSPORTER_WLDCRT = "UpaTransporter%";

    private static int counter = 0;
    private final String uddiUrl;
    private LinkedHashMap<String, BrokerTransportView> jobs = new LinkedHashMap<>();
    private final String wsName;


    public Broker(String uddiUrl, String wsName) {
        this.uddiUrl = uddiUrl;
        this.wsName = wsName;
    }

    public BrokerTransportView getCheapestTransporter(String origin, String destination, int maxPrice)
            throws UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception {
        JobView jw;
        BrokerTransportView chosenJob;
        BrokerTransportView tw = new BrokerTransportView(origin, destination, maxPrice, getUID());
        jobs.put(tw.getId(), tw);
        TransporterClient client;
        try {
            Collection<String> endpoints = getTransportersList();

            // iterate through the list of endpoints and select the lowest offer below maxPrice (if such exists)
            for (String endpoint : endpoints) {
                setClient(new TransporterClient(endpoint));
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
    
    /** used by the slave BrokerPort to save the job sent by the master
     */
    public void updateState(BrokerTVUpdateType btvu, int counter) {
		if( btvu != null) {
			BrokerTransportView btv = new BrokerTransportView(btvu);
    		jobs.put(btv.getId(), btv); // job is replaced if it already exists
		} else {
    		System.out.println("Broker.updateState(): received null BrokerTVUpdateType");
		}
    	if( counter < Broker.counter) {
    		System.out.println("Broker.updateState(): counter update is LOWER than current counter! bug!!");
    	} else {
    		Broker.counter = counter;
    	}
    }

    /** used by the master BrokerPort to get the job to be sent to the slave
     * don't forget to also update the counter
     */
    public BrokerTVUpdateType getBTVUpdate(String id) {
    	BrokerTransportView btv = jobs.get(id);
    	if(btv != null) {
    		return btv.toBTVUpdate();
    	}
    	return null;
    }

	public int getCounter() {
		return counter;
	}


    private void setClient(TransporterClient client) {
        this.client = client;
        // TODO
        //setClientReqContext(client);
    }

    private void setClientReqContext(TransporterClient client) {
        client.getRequestContext().put(AuthenticationHandler.CONTEXT_PRIVATE_KEY, getPrivateKey());
        client.getRequestContext().put(AuthenticationHandler.CONTEXT_SENDER_NAME, this.wsName);
    }

    public PrivateKey getPrivateKey() {
        try {
            return CertificateHelper.getPrivateKey(this.wsName);
        } catch (Exception e) {
            System.out.println("[FATAL!!!] Could not get private key");
            e.printStackTrace();
            return null;
        }
    }
}
