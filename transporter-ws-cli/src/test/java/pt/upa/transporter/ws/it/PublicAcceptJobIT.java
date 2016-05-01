package pt.upa.transporter.ws.it;

import org.junit.Test;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

import static org.junit.Assert.assertEquals;

/**
 * Test suite
 */
public class PublicAcceptJobIT extends PublicAbstractIT {

    /**
     * Inform a transporter that the client (such as broker-ws) decided to
     * accept the job offer.
     *
     * @throws Exception
     * @result The job's state is JobStateView.ACCEPTED.
     */
    @Test
    public void testAcceptJob() throws Exception {
        JobView jv = CLIENT.requestJob(CENTRO_1, SUL_1, PRICE_UPPER_LIMIT);
        jv = CLIENT.decideJob(jv.getJobIdentifier(), true);
        assertEquals(JobStateView.ACCEPTED, jv.getJobState());
    }

    /**
     * Try to invoke decideJob twice on the same job.
     *
     * @throws BadJobFault_Exception
     * @result Should throw exception because after the first call to
     * CLIENT.decideJob, the job's state is no longer
     * JobStateView.PROPOSED.
     */
    @Test(expected = BadJobFault_Exception.class)
    public void testAcceptDuplicateJob() throws Exception {
        JobView jv = CLIENT.requestJob(CENTRO_1, SUL_1, PRICE_UPPER_LIMIT);
        CLIENT.decideJob(jv.getJobIdentifier(), true);
        CLIENT.decideJob(jv.getJobIdentifier(), true);
    }

    /**
     * Try to invoke decideJob with an invalid (empty string) job identifier.
     *
     * @throws BadJobFault_Exception
     * @result Should throw exception because it does not make sense to decide
     * on a job without an associated identifier.
     */
    @Test(expected = BadJobFault_Exception.class)
    public void testAcceptInvalidJob() throws Exception {
        CLIENT.decideJob(EMPTY_STRING, true);
    }

    /**
     * Try to invoke decideJob with an invalid (null) job identifier.
     *
     * @throws BadJobFault_Exception
     * @result Should throw exception because it does not make sense to decide
     * on a job without an associated identifier.
     */
    @Test(expected = BadJobFault_Exception.class)
    public void testAcceptNullJob() throws Exception {
        CLIENT.decideJob(null, true);
    }

}
