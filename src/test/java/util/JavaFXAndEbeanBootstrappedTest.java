package util;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import javafx.embed.swing.JFXPanel;
import nl.juraji.imagemanager.util.io.db.EbeanInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InvocationEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 7-5-2019.
 * image-manager
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

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException {
        LOGGER.info("Loading Ebean migrations...");
        final EbeanServer server = Ebean.getDefaultServer();
        final File[] dbMigrations = new File(
                JavaFXAndEbeanBootstrappedTest.class.getResource("/dbmigration").toURI())
                .listFiles((dir, name) -> name.endsWith(".sql"));

        // Drop everything
        LOGGER.info("Drop everything in the database");
        server.sqlUpdate("DROP ALL OBJECTS [DELETE FILES];");

        // Rerun migrations
        if (dbMigrations != null) {
            for (File dbMigration : dbMigrations) {
                LOGGER.info("Re-running Ebean migration: " + dbMigration.getName());

                final byte[] bytes = Files.readAllBytes(dbMigration.toPath());
                final String sql = new String(bytes);

                server.sqlUpdate(sql);
            }
        }

        LOGGER.info("Starting test...");
    }
}
