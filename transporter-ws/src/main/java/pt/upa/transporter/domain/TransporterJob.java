package pt.upa.transporter.domain;

import pt.upa.transporter.exception.InvalidJobStateTransitionException;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

import java.util.Random;

/**
 * Extends the functionality of the JobView, by providing a better interface for job state transitions and constructors.
 */
public class TransporterJob extends JobView {
    private static Random rand = new Random();

    public TransporterJob(String compName, String orig, String dst, int price, String id, JobStateView jsw) {
        setJobIdentifier(id);
        setCompanyName(compName);
        setJobOrigin(orig);
        setJobDestination(dst);
        setJobPrice(price);
        setJobState(jsw);
    }

    public TransporterJob(String compName, String orig, String dst, int price, String id) {
        this(compName, orig, dst, price, id, JobStateView.PROPOSED);
    }

    public TransporterJob(String compName, String orig, String dst, int price) {
        this(compName, orig, dst, price, getRandJobId());
    }

    public TransporterJob(String compName, String orig, String dst, int price, JobStateView jsw) {
        this(compName, orig, dst, price, getRandJobId(), jsw);
    }

    /**
     * Advances to the next JobState, assumes that noting goes wrong and that the job will be accepted.
     * To reject a job, call {@link #rejectJob()}
     *
     * @return state_changed tells whether the state was changed withing the function
     */
    public boolean nextJobState() {
        boolean state_changed = true;
        switch (getJobState()) {
            case PROPOSED:
                setJobState(JobStateView.ACCEPTED);
                break;
                case ACCEPTED:
                setJobState(JobStateView.HEADING);
                break;
            case HEADING:
                setJobState(JobStateView.ONGOING);
                break;
            case ONGOING:
                setJobState(JobStateView.COMPLETED);
                break;
            default:
                // if it's REJECTED or COMPLETED, do nothing
                state_changed = false;
                break;
        }
        return state_changed;
    }

    // useful for Timer
    public boolean isCompleted() {
        return getJobState().equals(JobStateView.COMPLETED);
    }

    /**
     * Rejects a job. If the method is called when the job is at any other state, but
     * {@link pt.upa.transporter.ws.JobStateView#PROPOSED}, the method will throw an exception.
     * @throws InvalidJobStateTransitionException
     */
    public void rejectJob() throws InvalidJobStateTransitionException {
        if (getJobState() == JobStateView.PROPOSED) {
            setJobState(JobStateView.REJECTED);
        } else {
            // TODO:[priority_low] map JobStateView integer should to a String representation
            throw new InvalidJobStateTransitionException("Cannot reject a job when it's state is "
                    + JobStateView.ACCEPTED);
        }
    }

    /**
     * Generates a random job id.
     * @return the generated job id
     */
    public static String getRandJobId() {
        // For now, simply returns a String representation of a long. Any code generating random job Ids should
        // use this method, since if we change the way random Ids are created later on, the change propagates over
        // the whole codebase.
        return String.valueOf(rand.nextLong());
    }
}
