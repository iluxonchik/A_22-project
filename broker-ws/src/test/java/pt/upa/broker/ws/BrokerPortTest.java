package pt.upa.broker.ws;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import org.junit.*;
import pt.upa.broker.domain.Broker;
import pt.upa.broker.domain.BrokerTransportView;
import static org.junit.Assert.*;

import java.util.HashMap;

/**
 * Created by iluxonchik on 15-04-2016.
 */
public class BrokerPortTest {
    private BrokerPort brokerPort;

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    @Before
    public void setUp() {
        brokerPort = new BrokerPort();
    }

    @After
    public void tearDown() {
        brokerPort = null;
    }

    @Test(expected = InvalidPriceFault_Exception.class)
    public void priceFail() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        brokerPort.requestTransport("Lisboa", "Porto", -2);
    }

    @Test(expected = InvalidPriceFault_Exception.class)
    public void noOfferForPriceFail() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        new MockUp<Broker>() {
            @SuppressWarnings("unused")
            @Mock
            public BrokerTransportView getCheapestTransporter(String origin, String destination, int maxPrice) {
                return null;
            }
        };
        brokerPort.requestTransport("Lisboa", "Porto", 60);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void unknownLocationOriginFail() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        brokerPort.requestTransport("Compton", "Lisboa", 100);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void unknownLocationDestinationFail() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        brokerPort.requestTransport("Evora", "Harlem", 99);
    }

    @Test
    public void requestTransportSuccess() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        new MockUp<Broker>() {
            @SuppressWarnings("unused")
            @Mock
            public BrokerTransportView getCheapestTransporter(String origin, String destination, int maxPrice) {
                return new BrokerTransportView("Lisboa", "Porto", 50, "BROKER_1");
            }
        };
        assertEquals(brokerPort.requestTransport("Lisboa", "Porto", 60), "BROKER_1");
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void viewTransportFail() throws UnknownTransportFault_Exception {
        new MockUp<Broker>() {
            @SuppressWarnings("unused")
            @Mock
            public TransportView getJobById(String id) throws UnknownTransportFault_Exception {
                UnknownTransportFault faultInfo = new UnknownTransportFault();
                faultInfo.setId(id);
                throw new UnknownTransportFault_Exception("Job not found", faultInfo);
            }
        };
        brokerPort.viewTransport("BROKER_123");
    }

    @Test
    public void viewTransportSuccess() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception, UnknownTransportFault_Exception {
        HashMap<String, BrokerTransportView> jobs = new HashMap<>();
        new MockUp<Broker>() {

            @SuppressWarnings("unused")
            @Mock
            public BrokerTransportView getCheapestTransporter(String origin, String destination, int maxPrice) {
                jobs.put("BROKER_1", new BrokerTransportView(origin, destination, maxPrice, "BROKER_1"));
                return jobs.get("BROKER_1");
            }

            @SuppressWarnings("unused")
            @Mock
            public TransportView getJobById(String id) throws UnknownTransportFault_Exception {
                return jobs.get("BROKER_1");
            }
        };

        String jobId = brokerPort.requestTransport("Lisboa", "Porto", 90);
        assertEquals(jobs.get(jobId), brokerPort.viewTransport(jobId));
    }

    // tests

    /*
    @Test
    public void test() {
        new MockUp<TransporterClient>() {
            @SuppressWarnings("unused")
            @Mock
            private void createStub() {

            }
        }
    }*/
}
