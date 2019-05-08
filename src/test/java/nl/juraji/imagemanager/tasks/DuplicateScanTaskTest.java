package nl.juraji.imagemanager.tasks;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import org.junit.jupiter.api.Test;
import util.JavaFXAndEbeanBootstrappedTest;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Juraji on 8-5-2019.
 * image-manager
 */
class DuplicateScanTaskTest extends JavaFXAndEbeanBootstrappedTest {
    private final Path testImagesDirectory;
    private final EbeanServer db;

    public DuplicateScanTaskTest() throws URISyntaxException {
        this.testImagesDirectory = Paths.get(
                HashDirectoryTask.class.getResource("/test-images").toURI());
        this.db = Ebean.getDefaultServer();
    }

    @Test
    public void testDuplicateDetection() {
        // Setup test directory
    }
}
