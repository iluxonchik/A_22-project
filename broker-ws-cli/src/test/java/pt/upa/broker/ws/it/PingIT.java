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
public class PingIT extends AbstractBrokerIT {
    // members


    // initialization and clean-up for each test
	@Override
    @Before
    public void setUp() {
    }

	@Override
    @After
    public void tearDown() {
    }


    // tests

    @Test
    public void pingTest() {
    	String name = "MyName";
    	String result = broker.ping(name);
        assertEquals("Hello " + name + " !", result);
    }

}