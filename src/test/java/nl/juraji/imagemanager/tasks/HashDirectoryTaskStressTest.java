package nl.juraji.imagemanager.tasks;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalMetaData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import util.JavaFXAndEbeanBootstrappedTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Juraji on 7-5-2019.
 * image-manager
 */
@Disabled("Used during development for performance testing")
class HashDirectoryTaskStressTest extends JavaFXAndEbeanBootstrappedTest {
    private static final int NO_META_DATA = 1000;

    private final Path testImagesDirectory;
    private final EbeanServer db;

    public HashDirectoryTaskStressTest() throws URISyntaxException {
        this.testImagesDirectory = Paths.get(
                HashDirectoryTask.class.getResource("/test-images").toURI());
        this.db = Ebean.getDefaultServer();
    }

    @Test
    public void testHashingGreatAmountOfImages() throws IOException {
        // Setup test directory
        final LocalDirectory directory = new LocalDirectory();
        directory.setName("Test directory");
        directory.setLocationOnDisk(testImagesDirectory);

        // Setup test images
        IntStream.range(0, NO_META_DATA).forEach(i -> {
            final LocalMetaData testImageJpg = new LocalMetaData();
            testImageJpg.setPath(testImagesDirectory.resolve("jpg-image.jpg"));
            directory.getMetaData().add(testImageJpg);
        });

        // Task expects directory to be persisted in the database in order to update it
        directory.save();

        // Call hashing task
        final HashDirectoryTask hashDirectoryTask = new HashDirectoryTask(directory);
        hashDirectoryTask.call();

        final LocalDirectory directoryFromDb = db.find(LocalDirectory.class, directory.getId());
        assertNotNull(directoryFromDb);

        assertEquals(NO_META_DATA, directory.getMetaData().size());

        final byte[] testImageJpgHashBits = Files.readAllBytes(testImagesDirectory.resolve("jpg-image.jpg.hash.bin"));
        directory.getMetaData().forEach(metaData -> {
            assertNotNull(metaData.getHash());
            assertArrayEquals(testImageJpgHashBits, metaData.getHash().getBits());
        });
    }
}
