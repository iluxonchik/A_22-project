package pt.upa.broker.ws.it;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Test suite
 */
public class PublicPingIT extends PublicAbstractIT {

    // tests
    // assertEquals(expected, actual);

    // public String ping(String x)

    @Test
    public void pingTest() {
        assertNotNull(CLIENT.ping("test"));
    }

}
