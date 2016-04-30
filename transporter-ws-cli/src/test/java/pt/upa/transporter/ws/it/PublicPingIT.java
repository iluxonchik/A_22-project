package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

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
