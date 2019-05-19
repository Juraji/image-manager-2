package util;

import nl.juraji.imagemanager.util.io.db.EbeanInit;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Juraji on 10-5-2019.
 * image-manager
 */
public abstract class IMTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IMTest.class);

    @BeforeAll
    public static void setAWTToolkit() {
        // Load Ebean mode
        LOGGER.info("Setting java.awt.headless to True...");
        System.setProperty("java.awt.headless", "true");
    }

    @BeforeAll
    public static void setUpEbean() {
        // Load Ebean mode
        LOGGER.info("Initialize Ebean...");
        EbeanInit.initDataSource();
        LOGGER.info("Initialization completed!");
    }
}
