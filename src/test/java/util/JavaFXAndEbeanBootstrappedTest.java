package util;

import javafx.embed.swing.JFXPanel;
import nl.juraji.imagemanager.util.io.db.EbeanInit;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InvocationEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 7-5-2019.
 * image-manager
 *
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
        LOGGER.info("Initialize JavaFx in latch...");
        final CountDownLatch latch = new CountDownLatch(1);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                new InvocationEvent(Toolkit.getDefaultToolkit(), () -> {
                    new JFXPanel();
                    latch.countDown();
                }));

        if (!latch.await(1L, TimeUnit.SECONDS)) {
            throw new ExceptionInInitializerError();
        }

        LOGGER.info("Initialization completed!");
    }
}
