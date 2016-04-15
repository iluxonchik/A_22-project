package pt.upa.transporter.ws;

import org.junit.*;
import static org.junit.Assert.*;

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
