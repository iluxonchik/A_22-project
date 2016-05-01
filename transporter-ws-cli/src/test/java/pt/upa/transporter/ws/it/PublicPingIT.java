package pt.upa.transporter.ws.it;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Test suite
 */
public class PublicPingIT extends PublicAbstractIT {

    /**
     * Receive a non-null reply from the transporter that was pinged through
     * CLIENT.
     */
    @Test
    public void pingEmptyTest() {
        assertNotNull(CLIENT.ping("test"));
    }

}
