package pt.upa.broker.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class PublicViewTransportIT extends PublicAbstractIT {

    // public TransportView viewTransport(String id)
    // throws UnknownTransportFault_Exception

    @Test
    public void testTransportStateTransition() throws Exception {
        List<TransportStateView> tS = new ArrayList<>();

        tS.add(TransportStateView.HEADING);
        tS.add(TransportStateView.ONGOING);
        tS.add(TransportStateView.COMPLETED);

        System.out.println("Requesting transport...");
        String rt = CLIENT.requestTransport(CENTER_1, SOUTH_1, PRICE_SMALLEST_LIMIT);

        System.out.println("Transport requested...");
        TransportView vt = CLIENT.viewTransport(rt);
        assertEquals(vt.getState(), TransportStateView.BOOKED);

        int iter = 0;
        // NOTE: in the public test there was an "||" in the condition in for, but I believe that was a mistake and that
        // it should be "&&"
        for (int t = 0; t <= 3 * DELAY_UPPER && !tS.isEmpty(); t += TENTH_OF_SECOND) {
            System.out.println("Iteration " + iter++);
            System.out.println("t = " + t);

            Thread.sleep(TENTH_OF_SECOND);
            vt = CLIENT.viewTransport(rt);
            System.out.println(vt.getState());
            if (tS.contains(vt.getState()))
                tS.remove(vt.getState());
        }
        assertEquals(0, tS.size());
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void testViewInvalidTransport() throws Exception {
        CLIENT.viewTransport(null);
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void testViewNullTransport() throws Exception {
        CLIENT.viewTransport(EMPTY_STRING);
    }

}
