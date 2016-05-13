package pt.upa.broker;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.MasterBrokerPort;
import pt.upa.broker.ws.SlaveBrokerPort;

import javax.xml.ws.Endpoint;

public class BrokerApplication {
	public static final String REPLICATION_NONE = "none";
	public static final String REPLICATION_MASTER = "master";
	public static final String REPLICATION_SLAVE = "slave";

	
	private static void printUsage() {
        System.err.printf("Usage: java %s uddiURL wsName wsURL [none|((slave|master) other_wsURL)]%n", BrokerApplication.class.getName());
	}
	
    public static void main(String[] args) throws Exception {
        System.out.println(BrokerApplication.class.getSimpleName() + " starting...");

        if (args.length < 3) {
            System.err.println("Argument(s) missing!");
            printUsage();
            return;
        }
        
        String brokerType = args.length > 3 ? args[3].toLowerCase() : REPLICATION_NONE;
        if(brokerType.equals(REPLICATION_NONE) 
        		&& brokerType.equals(REPLICATION_MASTER) 
        		&& brokerType.equals(REPLICATION_SLAVE) ) {
            System.err.printf("Unknown replication mode '%s'%n", brokerType);
        	printUsage();
        }
        
        String otherURL;
        if((brokerType.equals(REPLICATION_MASTER) || brokerType.equals(REPLICATION_SLAVE)) && args.length <= 4) {
        	printUsage();
        	return;
        } else if( brokerType.equals(REPLICATION_MASTER)|| brokerType.equals(REPLICATION_SLAVE)) {
        	otherURL = args[4];
        } else {
        	otherURL = "invalid stuff to avoid uninitialized warning";
        }

        String uddiURL = args[0];
        String name = args[1];
        String url = args[2];

        Endpoint endpoint = null;
        UDDINaming uddiNaming = null;

        try {
        	BrokerPort bp;
        	if(brokerType.equals(REPLICATION_MASTER)) {
        		bp = new MasterBrokerPort(uddiURL, name, url, otherURL);
        	} else if(brokerType.equals(REPLICATION_SLAVE)) {
        		bp = new SlaveBrokerPort(uddiURL, name, url, otherURL);
        	} else {
        		bp = new BrokerPort(uddiURL, name, url);
        	}
        	
            endpoint = Endpoint.create(bp);
            
            // publish endpoint
            System.out.printf("Starting %s as %s%n", url, brokerType);
            endpoint.publish(url);

            // publish to UDDI
            System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
            uddiNaming = new UDDINaming(uddiURL);
            uddiNaming.rebind(name, url);

            // wait
            System.out.println("Awaiting connections");
            System.out.println("Press enter to shutdown");
            System.in.read();

        } catch (Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();

        } finally {
            try {
                if (endpoint != null) {
                    // stop endpoint
                    endpoint.stop();
                    System.out.printf("Stopped %s%n", url);
                }
            } catch (Exception e) {
                System.out.printf("Caught exception when stopping: %s%n", e);
            }
            try {
                if (uddiNaming != null) {
                    // delete from UDDI
                    uddiNaming.unbind(name);
                    System.out.printf("Deleted '%s' from UDDI%n", name);
                }
            } catch (Exception e) {
                System.out.printf("Caught exception when deleting: %s%n", e);
            }
        }
    }
}
