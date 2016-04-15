package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.cli.BrokerClient;

import static org.junit.Assert.*;

/**
 *  Integration Test 
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class AbstractBrokerIT {
	protected static final String uddiURL = "http://localhost:9090";
	protected static final String wsName = "UpaBroker";

    // static members
	protected static BrokerPortType broker;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    	broker = new BrokerClient(uddiURL, wsName);
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	broker = null;
    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    // tests
    // implement in subclasses

}