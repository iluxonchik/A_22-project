package pt.upa.transporter.ws;

import org.junit.*;
import static org.junit.Assert.*;
import mockit.*;
import pt.upa.shared.Region;

/* Unitary tests */

public class TransporterPortTest extends AbstractTest {
    private TransporterPort centerSouthPort;
    private TransporterPort centerNorthPort;

    @Before
    public void populate() {
	    centerSouthPort = new TransporterPort("UpaTransporter1");
	    centerNorthPort = new TransporterPort("UpaTransporter2");
    }

    @After
    public void tearDown() {
        centerSouthPort = null;
        centerSouthPort = null;
    }


    @Test
    public void priceOfferTestSuccess() throws BadLocationFault_Exception, BadPriceFault_Exception {
        int even_offer = 90;
        int odd_offer = 13;
        int offer_price;

        // Even transporter
        offer_price = centerNorthPort.requestJob("Lisboa", "Porto", odd_offer).getJobPrice();
        assertTrue("Even transporter responded with a price (" + offer_price +
                " <= odd offer price (" + odd_offer + ")", offer_price > odd_offer);

        offer_price = centerNorthPort.requestJob("Lisboa", "Porto", even_offer).getJobPrice();
        assertTrue("Even transporter responded with a price (" + offer_price +
                " >= even offer price (" + even_offer + ")", offer_price < even_offer);

        // Odd transporter
        offer_price = centerSouthPort.requestJob("Faro", "Lisboa", odd_offer).getJobPrice();
        assertTrue("Odd transporter responded with a price (" + offer_price +
                " >= odd offer price (" + odd_offer + ")", offer_price < odd_offer);

        offer_price = centerSouthPort.requestJob("Faro", "Lisboa", even_offer).getJobPrice();
        assertTrue("Odd transporter responded with a price (" + offer_price +
                " <= even offer price (" + even_offer + ")", offer_price > odd_offer);

        // offer above 100
        assertNull(centerSouthPort.requestJob("Lisboa", "Faro", 101));

        // offer at 100
        offer_price = centerNorthPort.requestJob("Castelo Branco", "Braga", 100).getJobPrice();
        assertTrue("Even transporter responded with a price (" + offer_price +
                " >= odd offer price (" + 100 + ")", offer_price < 100);


        // offer at 10
        offer_price = centerSouthPort.requestJob("Santarém", "Portalegre", 10).getJobPrice();
        assertTrue("Odd transporter responded with a price (" + offer_price +
                " >= 10 to an offer of 10 or less (" + 10 + ")", offer_price < 10);
    }

    @Test
    public void locationFail1() {

    }
	
    @Test(expected = BadLocationFault_Exception.class)
    public void unknownLocationFail() throws BadLocationFault_Exception, BadPriceFault_Exception {
        centerSouthPort.requestJob("Lisboa", "Porto", 100);
	}

	
}
