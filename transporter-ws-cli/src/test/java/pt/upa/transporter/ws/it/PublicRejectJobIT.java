package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;


/**
 * Test suite
 */
public class PublicRejectJobIT extends PublicAbstractIT {

	/**
	 * Create a job (with valid arguments), decide on it (reject) and check that
	 * its state changed to JobStateView.REJECTED.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRejectJob() throws Exception {
		JobView jv = CLIENT.requestJob(CENTRO_1, SUL_1, PRICE_SMALLEST_LIMIT);
		jv = CLIENT.decideJob(jv.getJobIdentifier(), false);
		assertEquals(JobStateView.REJECTED, jv.getJobState());
	}

	/**
	 * Create a job (with valid arguments) and attempt to decide (reject) on it
	 * twice.
	 * 
	 * @result Should throw BadJobFault_Exception because it does not make sense
	 *         to decide on an already decided job.
	 * @throws Exception
	 */
	@Test(expected = BadJobFault_Exception.class)
	public void testRejectDuplicateJob() throws Exception {
		JobView jv = CLIENT.requestJob(CENTRO_2, SUL_2, PRICE_SMALLEST_LIMIT);
		CLIENT.decideJob(jv.getJobIdentifier(), false);
		CLIENT.decideJob(jv.getJobIdentifier(), false);
	}

	/**
	 * Invoke CLIENT.decideJob on an invalid (empty string) job identifier.
	 * 
	 * @result Should throw BadJobFault_Exception as the job is invalid.
	 * @throws Exception
	 */
	@Test(expected = BadJobFault_Exception.class)
	public void testRejectInvalidJob() throws Exception {
		CLIENT.decideJob(EMPTY_STRING, false);
	}

	/**
	 * Invoke CLIENT.decideJob on an invalid (null) job identifier.
	 * 
	 * @result Should throw BadJobFault_Exception as the job is invalid.
	 * @throws Exception
	 */
	@Test(expected = BadJobFault_Exception.class)
	public void testRejectNullJob() throws Exception {
		CLIENT.decideJob(null, false);
	}

}
