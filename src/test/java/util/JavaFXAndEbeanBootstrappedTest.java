package util;

import javafx.application.Application;
import nl.juraji.imagemanager.util.io.db.EbeanInit;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Juraji on 7-5-2019.
 * image-manager
 * <p>
 * Initializes Ebean with test-ebean.properties and the JavaFX Toolkit
 * Note that the database is not cleaned before each test, so test data remains
 */
public abstract class JavaFXAndEbeanBootstrappedTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JavaFXAndEbeanBootstrappedTest.class);

    @BeforeAll
    public static void setUpClass() throws InterruptedException {
        // Load Ebean mode
        LOGGER.info("Initialize Ebean...");
        EbeanInit.initDataSource();

        // Setup JavaFX test framework
        LOGGER.info("Initialize JavaFx...");
        FXInit.initializeFXToolkit();

        LOGGER.info("Initialization completed!");
    }

}
