package pt.upa.transporter.ws.it;

import org.junit.Test;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test suite
 */
public class PublicJobStatusIT extends PublicAbstractIT {

    /**
     * Test an invocation of jobStatus on an invalid (empty string) job
     * identifier. Implementation-dependent.
     */
    @Test
    public void testInvalidJobStatus() {
        assertEquals(null, CLIENT.jobStatus(EMPTY_STRING));
    }

    /**
     * Test an invocation of jobStatus on an invalid (null) job identifier.
     * Implementation-dependent.
     */
    @Test
    public void testNullJobStatus() throws Exception {
        assertEquals(null, CLIENT.jobStatus(null));
    }

    /**
     * 1 - Request a job with valid origin, destination and price, check that
     * its initial state is JobStateView.PROPOSED.
     * 2 - Decide (accept) on the
     * created job and check that its state changed to JobStateView.ACCEPTED.
     * 3 - Try to decide (reject) on the previously-accepted job.
     *
     * @throws Exception
     * @result At the end of the test the job rejection should have failed.
     */
    @Test
    public void testJobStatus1() throws Exception {
        JobView jv = CLIENT.requestJob(CENTRO_1, SUL_1, PRICE_UPPER_LIMIT);
        JobStateView jsv = CLIENT.jobStatus(jv.getJobIdentifier()).getJobState();
        assertEquals(JobStateView.PROPOSED, jsv);

        jv = CLIENT.decideJob(jv.getJobIdentifier(), true);
        assertEquals(JobStateView.ACCEPTED, jv.getJobState());

        try {
            CLIENT.decideJob(jv.getJobIdentifier(), false);
        } catch (BadJobFault_Exception e) {
            // expected
        }
    }

    /**
     * Create two more jobs (with valid arguments), check that their states are
     * correct and that the number of jobs returned by CLIENT.listJobs increased by two.
     *
     * @throws Exception
     */
    @Test
    public void testJobStatus2() throws Exception {
        int initialNrJobs = CLIENT.listJobs().size();

        JobView jv1 = CLIENT.requestJob(CENTRO_1, SUL_1, PRICE_UPPER_LIMIT);
        JobView jv2 = CLIENT.requestJob(CENTRO_2, SUL_2, PRICE_UPPER_LIMIT - 1);
        jv2 = CLIENT.decideJob(jv2.getJobIdentifier(), true);
        assertEquals(initialNrJobs + 2, CLIENT.listJobs().size());

        JobStateView jsv1 = CLIENT.jobStatus(jv1.getJobIdentifier()).getJobState();
        assertEquals(JobStateView.PROPOSED, jsv1);
        JobStateView jsv2 = CLIENT.jobStatus(jv2.getJobIdentifier()).getJobState();
        assertEquals(JobStateView.ACCEPTED, jsv2);
    }

    /**
     * Clear the set of jobs, create a new (with valid arguments) job, decide on
     * it (accept it) and check that its state is progressively-updated until it
     * is JobStateView.COMPLETED.
     *
     * @throws Exception
     */
    @Test
    public void testJobStateTransition() throws Exception {
        List<JobStateView> jobStates = new ArrayList<>();
        jobStates.add(JobStateView.HEADING);
        jobStates.add(JobStateView.ONGOING);
        jobStates.add(JobStateView.COMPLETED);
        CLIENT.clearJobs();

        JobView jv = CLIENT.requestJob(CENTRO_1, SUL_1, PRICE_UPPER_LIMIT);
        JobStateView jsv1 = CLIENT.jobStatus(jv.getJobIdentifier()).getJobState();
        assertEquals(JobStateView.PROPOSED, jsv1);

        jv = CLIENT.decideJob(jv.getJobIdentifier(), true);
        JobStateView jsv2 = CLIENT.jobStatus(jv.getJobIdentifier()).getJobState();
        assertEquals(JobStateView.ACCEPTED, jsv2);

        for (int t = 1; t <= 15; t++) {
            Thread.sleep(DELAY_LOWER);
            jv = CLIENT.jobStatus(jv.getJobIdentifier());
            if (jobStates.contains(jv.getJobState()))
                jobStates.remove(jv.getJobState());
        }
        assertEquals(0, jobStates.size());
        // this test does not strictly validate the correct sequence:
        // HEADING -> ONGOING -> COMPLETED
        // it just checks if the job was at each state
    }

}
