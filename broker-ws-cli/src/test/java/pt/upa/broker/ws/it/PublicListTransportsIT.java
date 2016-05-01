package pt.upa.broker.ws.it;

import org.junit.Test;
import pt.upa.broker.ws.TransportView;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PublicListTransportsIT extends PublicAbstractIT {

    // tests
    // assertEquals(expected, actual);

    // public List<TransportView> listTransports();

    @Test
    public void testListTransports() throws Exception {
        CLIENT.clearTransports();// To start fresh
        String j1 = CLIENT.requestTransport(SOUTH_1, CENTER_1, PRICE_SMALLEST_LIMIT);
        String j2 = CLIENT.requestTransport(NORTH_1, CENTER_1, PRICE_SMALLEST_LIMIT);
        String j3 = CLIENT.requestTransport(CENTER_1, CENTER_2, PRICE_SMALLEST_LIMIT);
        TransportView jtv1 = CLIENT.viewTransport(j1);
        TransportView jtv2 = CLIENT.viewTransport(j2);
        CLIENT.viewTransport(j3);

        List<TransportView> tList = CLIENT.listTransports();
        assertEquals(3, tList.size());

        TransportView tv1 = tList.get(0);
        assertTrue("Expected origin = " + jtv1.getOrigin() + " Obtained origin: " + tv1.getOrigin(), (jtv1.getId().equals(tv1.getId())) && jtv1.getOrigin().equals(tv1.getOrigin())
                && jtv1.getDestination().equals(tv1.getDestination()) && jtv1.getPrice().equals(tv1.getPrice())
                && jtv1.getState().toString().equals(tv1.getState().toString()));
        TransportView tv2 = tList.get(1);
        assertTrue((jtv2.getId().equals(tv2.getId())) && jtv2.getOrigin().equals(tv2.getOrigin())
                && jtv2.getDestination().equals(tv2.getDestination()) && jtv2.getPrice().equals(tv2.getPrice())
                && jtv2.getState().toString().equals(tv2.getState().toString()));
    }

}
