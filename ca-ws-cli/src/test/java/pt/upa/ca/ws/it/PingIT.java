package pt.upa.ca.ws.it;

import org.junit.*;
import static org.junit.Assert.*;

public class PingIT extends AbstractCAIT {

    @Test
    public void pingTestSuccess() {
        final String expected = "Hello Dr.Dre!";
        final String res = ca.ping("Dr.Dre");

        assertEquals(expected, res);
    }

}