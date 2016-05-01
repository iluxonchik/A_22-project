package pt.upa.transporter.ws.it;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.io.IOException;
import java.util.Properties;

/**
 * Integration Test suite abstract class. Test classes inherit this one to
 * better configure and prepare each test.
 * <p>
 * NOTE: this assumes that there is a transporter with name "UpaTransporter1" registered in UDDI.
 */
public class PublicAbstractIT {

    protected static final int PRICE_UPPER_LIMIT = 100;
    protected static final int PRICE_SMALLEST_LIMIT = 10;
    protected static final int UNITARY_PRICE = 1;
    protected static final int ZERO_PRICE = 0;
    protected static final int INVALID_PRICE = -1;
    protected static final String CENTRO_1 = "Lisboa";
    protected static final String SUL_1 = "Beja";
    protected static final String CENTRO_2 = "Coimbra";
    protected static final String SUL_2 = "Portalegre";
    protected static final String EMPTY_STRING = "";
    protected static final int DELAY_LOWER = 1000; // milliseconds
    protected static final int DELAY_UPPER = 5000; // milliseconds
    private static final String TEST_PROP_FILE = "/test.properties";
    protected static TransporterClient CLIENT;
    private static Properties PROPS;

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
        String wsName = PROPS.getProperty("ws.name"); // read note in javadoc of this class
        String wsURL = PROPS.getProperty("ws.url");

        // Note: CLIENT is defined to be an odd transporter in the pom file
        // (UpaTransporter1).

        if ("true".equalsIgnoreCase(uddiEnabled)) {
            CLIENT = new TransporterClient(uddiURL, wsName);
        } else {
            CLIENT = new TransporterClient(wsURL);
        }

    }

    @AfterClass
    public static void cleanup() {
        CLIENT = null;
    }

}
