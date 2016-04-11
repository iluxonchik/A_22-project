package pt.upa.shared;

import org.junit.*;
import static org.junit.Assert.*;

public class RegionTest {

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void evenUpaTransporterByName() {
        assertTrue("UpaTransporter22 - Porto", Region.isKnownByTransporter("UpaTransorter22", "Porto"));
        assertTrue("OtherNamesWorkToo$#22234 - Braga", Region.isKnownByTransporter("OtherNamesWorkToo$#22234", "Braga"));
        assertFalse("UpaTransporter22 - Setúbal", Region.isKnownByTransporter("UpaTransorter22", "Setúbal"));
        assertFalse("UpaTransporter11 - Porto" ,Region.isKnownByTransporter("UpaTransorter11", "Porto"));
    }

}