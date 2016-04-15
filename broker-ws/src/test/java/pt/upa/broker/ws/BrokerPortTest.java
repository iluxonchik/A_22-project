package pt.upa.broker.ws;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import org.junit.*;
import pt.upa.broker.domain.Broker;
import pt.upa.broker.domain.BrokerTransportView;

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
