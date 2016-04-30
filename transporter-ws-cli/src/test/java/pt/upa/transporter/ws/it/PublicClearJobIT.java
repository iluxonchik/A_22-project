package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test suite - simple test of clearing a transporter's jobs.
 */
public class PublicClearJobIT extends PublicAbstractIT {

	/**
	 * Delete all jobs in a transporter. There shouldn't be any errors.
	 * 
	 * @result CLIENT.listJobs() will return 0 after invoking
	 *         CLIENT.clearJobs().
	 * @throws Exception
	 */
	@Test
	public void testClearJob() throws Exception {
		CLIENT.clearJobs();
		assertEquals(0, CLIENT.listJobs().size());
	}

}
