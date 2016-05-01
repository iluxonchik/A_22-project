package pt.upa.broker.ws.it;

import org.junit.Test;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

import static org.junit.Assert.assertEquals;

public class PublicClearTransportsIT extends PublicAbstractIT {

    // tests
    // assertEquals(expected, actual);

    // public void clearTransports();

    @Test(expected = UnknownTransportFault_Exception.class)
    public void testClearTransports() throws Exception {
        String rt = CLIENT.requestTransport(CENTER_1, SOUTH_1, PRICE_SMALLEST_LIMIT);
        CLIENT.clearTransports();
        assertEquals(0, CLIENT.listTransports().size());
        CLIENT.viewTransport(rt);
    }
}
