package pt.upa.broker.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import pt.upa.broker.ws.cli.BrokerClient;

/**
 * Integration Test suite abstract class
 */
public class PublicAbstractIT {

	private static final String TEST_PROP_FILE = "/test.properties";

	private static Properties PROPS;
	protected static BrokerClient CLIENT;

	protected static int PRICE_UPPER_LIMIT = 100;
	protected static int PRICE_SMALLEST_LIMIT = 10;

	protected static int INVALID_PRICE = -1;
	protected static int ZERO_PRICE = 0;
	protected static int UNITARY_PRICE = 1;

	protected static int ODD_INCREMENT = 1;
	protected static int EVEN_INCREMENT = 2;
	
	protected static final String SOUTH_1 = "Beja";
	protected static final String SOUTH_2 = "Portalegre";
	
	protected static final String CENTER_1 = "Lisboa";
	protected static final String CENTER_2 = "Coimbra";
	
	protected static final String NORTH_1 = "Porto";
	protected static final String NORTH_2 = "Braga";
	
	protected static final String EMPTY_STRING = "";
	
	protected static final int DELAY_LOWER = 1000; // = 1 second
	protected static final int DELAY_UPPER = 5000; // = 5 seconds
    protected static final int TENTH_OF_SECOND = 100;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		PROPS = new Properties();
		try {
			PROPS.load(PublicAbstractIT.class.getResourceAsStream(TEST_PROP_FILE));
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}
		String uddiEnabled = PROPS.getProperty("uddi.enabled");
		String uddiURL = PROPS.getProperty("uddi.url");
		String wsName = PROPS.getProperty("ws.name");
		String wsURL = PROPS.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			CLIENT = new BrokerClient(uddiURL, wsName);
		} else {
			CLIENT = new BrokerClient(wsURL);
		}
	}

	@AfterClass
	public static void cleanup() {
		CLIENT = null;
	}

}
