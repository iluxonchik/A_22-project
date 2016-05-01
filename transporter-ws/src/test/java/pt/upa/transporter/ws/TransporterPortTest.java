package pt.upa.transporter.ws;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test(expected = BadPriceFault_Exception.class)
    public void priceFail() throws BadLocationFault_Exception, BadPriceFault_Exception {
        centerNorthPort.requestJob("Aveiro", "Porto", -1);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void unknownLocationFail1() throws BadLocationFault_Exception, BadPriceFault_Exception {
        centerSouthPort.requestJob("Lisboa", "Porto", 100);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void unknownLocationFail2() throws BadLocationFault_Exception, BadPriceFault_Exception {
        centerNorthPort.requestJob("Evora", "Viana do Castelo", 26);
    }

    @Test
    public void decideJobRejectSuccess() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
        JobView offer = centerSouthPort.requestJob("Aveiro", "Beja", 45);
        centerSouthPort.decideJob(offer.getJobIdentifier(), false);
        offer = centerSouthPort.jobStatus(offer.getJobIdentifier());
        assertEquals("Offer has not transitioned to rejected state", offer.getJobState(), JobStateView.REJECTED);
    }

    @Test
    public void noSuchJobSuccess() {
        assertNull(centerSouthPort.jobStatus("The Game"));
    }

    @Test
    public void timerTestSuccess() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception, InterruptedException {
        JobView offer = centerSouthPort.requestJob("Evora", "Viseu", 11);
        centerSouthPort.decideJob(offer.getJobIdentifier(), true);
        Thread.sleep(1000 * 5 * 3); // TODO: use a better solution that waiting 15 seconds
        offer = centerSouthPort.jobStatus(offer.getJobIdentifier());
        assertEquals("Timer did not make the required transitions", JobStateView.COMPLETED, offer.getJobState());
    }

}
