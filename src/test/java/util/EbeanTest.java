package util;

import nl.juraji.imagemanager.util.io.db.EbeanInit;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Juraji on 7-5-2019.
 * image-manager
 * <p>
 * Initializes Ebean with test-ebean.properties and the JavaFX Toolkit
 * Note that the database is not cleaned before each test, so test data remains
 */
public abstract class EbeanTest extends MockFXTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(EbeanTest.class);

    @BeforeClass
    public static void setUpEbean() {
        // Load Ebean mode
        LOGGER.info("Initialize Ebean...");
        EbeanInit.initDataSource();
        LOGGER.info("Initialization completed!");
    }
}
