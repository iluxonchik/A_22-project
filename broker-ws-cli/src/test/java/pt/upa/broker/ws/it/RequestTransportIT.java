package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;

import static org.junit.Assert.*;

/**
 *  Integration Test 
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class RequestTransportIT extends AbstractBrokerIT {
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
    public void testTransport1() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	String origin = "Porto";
    	String destination = "Lisboa";
    	int price = 100;
    	String result = broker.requestTransport(origin, destination, price);
        assertNotNull(result);
    }

}