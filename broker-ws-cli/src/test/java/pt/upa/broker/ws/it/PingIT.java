package pt.upa.broker.ws.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Integration Test
 * <p>
 * Invoked by Maven in the "verify" life-cycle phase
 * Should invoke "live" remote servers
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