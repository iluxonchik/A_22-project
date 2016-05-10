package pt.upa.ca.ws.it;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import pt.upa.ca.ws.CAPortType;
import pt.upa.ca.ws.cli.CAClient;

/**
 * Abstract Integration Test
 */
public class AbstractCAIT {
    protected static final String uddiURL = "http://localhost:9090";
    protected static final String wsName = "CA";

    // static members
    protected static CAPortType ca;

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        ca = new CAClient(uddiURL, wsName);
    }

    @AfterClass
    public static void oneTimeTearDown() {
        ca = null;
    }


    // initialization and clean-up for each test
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

}
