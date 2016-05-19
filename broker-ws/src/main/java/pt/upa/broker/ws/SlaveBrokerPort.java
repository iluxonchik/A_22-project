package pt.upa.broker.ws;

import java.util.Timer;
import java.util.TimerTask;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;



@WebService(
        endpointInterface = "pt.upa.broker.ws.BrokerPortType",
        wsdlLocation = "broker.2_0.wsdl",
        name = "BrokerSlave",
        portName = "BrokerPort",
        targetNamespace = "http://ws.broker.upa.pt/",
        serviceName = "BrokerService"
)
@HandlerChain(file = "/broker_handler-chain.xml")
public class SlaveBrokerPort extends BrokerPort {

	protected BrokerPortType master;
	protected String masterURL;
    protected Timer watchdogTimer = new Timer();
	
    public SlaveBrokerPort(String uddiUrl, String wsName, String slaveURL, String masterURL) throws JAXRException {
    	super(uddiUrl, wsName, slaveURL);
    	this.masterURL = masterURL;
    	//master = getRemoteBroker(masterURL); // XXX NOT USED: master will ping slave. slave does not need to contact master
    }

    private class WatchdogTask extends TimerTask {
        @Override
        public void run() {
        	// no ping for more than 2*WATCH_DELAY_MS
        	//assume master is dead
        	synchronized (watchdogTimer) {
        		try{
        		watchdogTimer.cancel();
    	    	watchdogTimer = new Timer();
        		} catch (IllegalStateException e) {
        			
        		}
        		replaceMaster();
        	}
        }
    }
    
    private void replaceMaster() {
    	try {
    		System.out.printf("REPLACING MASTER%nPublishing '%s' to UDDI at %s%n", wsName, wsUrl);
    		UDDINaming uddiNaming = new UDDINaming(getUddiUrl());
    		uddiNaming.rebind(wsName, wsUrl);
    	} catch (JAXRException e) {
    		throw new RuntimeException(e);
    	}
    }
    
    @Override
    public String ping(String name) {
    	synchronized (watchdogTimer) {
    		try{
	    	watchdogTimer.cancel();
	    	watchdogTimer = new Timer();
			} catch (IllegalStateException e) {
				
			}
	        watchdogTimer.schedule(new WatchdogTask(), 2 * WATCH_DELAY_MS);
    	}
        return "Hello " + name + " !";
    }

}
