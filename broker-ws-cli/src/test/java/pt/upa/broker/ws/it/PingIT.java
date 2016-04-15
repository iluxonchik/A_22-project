package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;

import static org.junit.Assert.*;

/**
 *  Integration Test 
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class PingIT {

    // static members
	private static BrokerPortType broker;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    	//broker = new BrokerClient();
    }

    @AfterClass
    public static void oneTimeTearDown() {

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

    @Test
    public void test() {

        // assertEquals(expected, actual);
        // if the assert fails, the test fails
    }

}