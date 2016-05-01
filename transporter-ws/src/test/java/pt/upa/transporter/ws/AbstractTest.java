package pt.upa.transporter.ws;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractTest {

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }

    // initialization and clean-up for each test
    @Before
    public void populate() {
    }

    @After
    public void tearDown() {
    }


}
