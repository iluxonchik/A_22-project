package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.upa.transporter.ws.cli.TransporterClient;

import static org.junit.Assert.assertNotNull;

/**
 * Integration Test example
 * <p>
 * Invoked by Maven in the "verify" life-cycle phase
 * Should invoke "live" remote servers
 */
public class PingIT {

    private static TransporterClient client;

    @BeforeClass
    public static void oneTimeSetUp() {
        client = new TransporterClient("http://localhost:9090", "UpaTransporter1");
    }

    @AfterClass
    public static void oneTimeTearDown() {
        client = null;
    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    // tests

    @Test
    public void test() {
        assertNotNull("ping test", "Hi UpaTransporter1");
    }

}
