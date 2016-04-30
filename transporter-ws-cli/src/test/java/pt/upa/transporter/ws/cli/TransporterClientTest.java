package pt.upa.transporter.ws.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.registry.JAXRException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.exception.TransporterClientException;

/**
 * Test suite
 */
public class TransporterClientTest {

	// static members

	private static final String uddiURL = "http://localhost:9090";
	private static final String wsName = "TransporterWebService";
	private static final String wsURL = "http://localhost:8080/transporter-ws/endpoint";

	// one-time initialization and clean-up

	@BeforeClass
	public static void oneTimeSetUp() {
	}

	@AfterClass
	public static void oneTimeTearDown() {
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
	// assertEquals(expected, actual);

	/**
	 * Checks if TransporterClient is correctly instantiating UDDINaming and
	 * looking up services with the given name.
	 * 
	 * @param uddiNaming
	 *            the mocked object
	 * @result TransporterClient should instantiate one UDDDINaming object and
	 *         call the lookup method once.
	 * @throws Exception
	 */
	@Test
	public void testMockUddi(@Mocked final UDDINaming uddiNaming) throws Exception {

		// Preparation code not specific to JMockit, if any.

		// an "expectation block"
		// One or more invocations to mocked types, causing expectations to be
		// recorded.
		new Expectations() {
			{
				new UDDINaming(uddiURL);
				uddiNaming.lookup(wsName);
				result = wsURL;
			}
		};

		// Unit under test is exercised.
		new TransporterClient(uddiURL, wsName);

		// a "verification block"
		// One or more invocations to mocked types, causing expectations to be
		// verified.
		new Verifications() {
			{
				// Verifies that zero or one invocations occurred, with the
				// specified argument value:
				new UDDINaming(uddiURL);
				uddiNaming.lookup(wsName);
				maxTimes = 1;
				uddiNaming.unbind(null);
				maxTimes = 0;
				uddiNaming.bind(null, null);
				maxTimes = 0;
				uddiNaming.rebind(null, null);
				maxTimes = 0;
			}
		};

		// Additional verification code, if any, either here or before the
		// verification block.
	}

	/**
	 * Checks if TransporterClient correctly checks if a service with a given
	 * name does not exist in UDDI.
	 * 
	 * @param uddiNaming
	 *            the mocked object
	 * @result TransporterClientException is expected with a specific message.
	 * @throws Exception
	 */
	@Test
	public void testMockUddiNameNotFound(@Mocked final UDDINaming uddiNaming) throws Exception {

		// Preparation code not specific to JMockit, if any.

		// an "expectation block"
		// One or more invocations to mocked types, causing expectations to be
		// recorded.
		new Expectations() {
			{
				new UDDINaming(uddiURL);
				uddiNaming.lookup(wsName);
				result = null;
			}
		};

		// Unit under test is exercised.
		try {
			new TransporterClient(uddiURL, wsName);
			fail();

		} catch (TransporterClientException e) {
			final String expectedMessage = String.format("Service with name %s not found on UDDI at %s", wsName,
					uddiURL);
			assertEquals(expectedMessage, e.getMessage());
		}

		// a "verification block"
		// One or more invocations to mocked types, causing expectations to be
		// verified.
		new Verifications() {
			{
				// Verifies that zero or one invocations occurred, with the
				// specified argument value:
				new UDDINaming(uddiURL);
				uddiNaming.lookup(wsName);
				maxTimes = 1;
			}
		};

		// Additional verification code, if any, either here or before the
		// verification block.
	}

	/**
	 * Checks if TransporterClient correctly checks if UDDI is not available.
	 * 
	 * @param uddiNaming
	 *            the mocked object
	 * @result TransporterClientException is expected with a specific message.
	 * @throws Exception
	 */
	@Test
	public void testMockUddiServerNotFound(@Mocked final UDDINaming uddiNaming) throws Exception {

		// Preparation code not specific to JMockit, if any.

		// an "expectation block"
		// One or more invocations to mocked types, causing expectations to be
		// recorded.
		new Expectations() {
			{
				new UDDINaming(uddiURL);
				result = new JAXRException("created for testing");
			}
		};

		// Unit under test is exercised.
		try {
			new TransporterClient(uddiURL, wsName);
			fail();

		} catch (TransporterClientException e) {
			assertTrue(e.getCause() instanceof JAXRException);
			final String expectedMessage = String.format("Client failed lookup on UDDI at %s!", uddiURL);
			assertEquals(expectedMessage, e.getMessage());
		}

		// a "verification block"
		// One or more invocations to mocked types, causing expectations to be
		// verified.
		new Verifications() {
			{
				// Verifies that zero or one invocations occurred, with the
				// specified argument value:
				new UDDINaming(uddiURL);
			}
		};

		// Additional verification code, if any, either here or before the
		// verification block.
	}

}
