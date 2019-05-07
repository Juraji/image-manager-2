package nl.juraji.imagemanager.tasks;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import nl.juraji.imagemanager.model.domain.hashes.Contrast;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalMetaData;
import org.junit.jupiter.api.Test;
import util.JavaFXAndEbeanBootstrappedTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Juraji on 7-5-2019.
 * image-manager
 */
class HashDirectoryTaskTest extends JavaFXAndEbeanBootstrappedTest {
    private final Path testImagesDirectory;
    private final EbeanServer db;

    public HashDirectoryTaskTest() throws URISyntaxException {
        this.testImagesDirectory = Paths.get(
                HashDirectoryTask.class.getResource("/test-images").toURI());
        this.db = Ebean.getDefaultServer();
    }

    @Test
    public void testHashingOfImages() throws IOException {
        // Setup test directory
        final LocalDirectory directory = new LocalDirectory();
        directory.setName("Test directory");
        directory.setLocationOnDisk(testImagesDirectory);

        // Setup test images
        final LocalMetaData testImageJpg = new LocalMetaData();
        testImageJpg.setDirectory(directory);
        testImageJpg.setPath(testImagesDirectory.resolve("test-image.jpg"));
        final LocalMetaData testImagePng = new LocalMetaData();
        testImagePng.setDirectory(directory);
        testImagePng.setPath(testImagesDirectory.resolve("test-image.png"));
        final LocalMetaData testImageGifStill = new LocalMetaData();
        testImageGifStill.setDirectory(directory);
        testImageGifStill.setPath(testImagesDirectory.resolve("test-image-still.gif"));
        final LocalMetaData testImageGifFramed = new LocalMetaData();
        testImageGifFramed.setDirectory(directory);
        testImageGifFramed.setPath(testImagesDirectory.resolve("test-image-framed.gif"));

        directory.getMetaData().add(testImageJpg);
        directory.getMetaData().add(testImagePng);
        directory.getMetaData().add(testImageGifStill);
        directory.getMetaData().add(testImageGifFramed);

        // Task expects directory to be persisted in the database in order to update it
        directory.save();


        // Call hashing task
        final HashDirectoryTask hashDirectoryTask = new HashDirectoryTask(directory);
        hashDirectoryTask.call();

        // Check hashes
        // Check JPG image ---------------------------------------------------------------------------------------------
        // Reload entity from the database, to make sure it persisted
        final LocalMetaData testImageJpgDb = db.find(LocalMetaData.class, testImageJpg.getId());
        assertNotNull(testImageJpgDb);

        // Assert a hash exists
        assertNotNull(testImageJpgDb.getHash());

        // Assert the correct contrast
        assertEquals(Contrast.DARK, testImageJpgDb.getHash().getContrast());

        // Assert the hashed bits are correct
        final byte[] testImageJpgHashBits = Files.readAllBytes(testImagesDirectory.resolve("test-image.jpg.hash.bin"));
        assertArrayEquals(testImageJpgHashBits, testImageJpgDb.getHash().getBits());

        // Check PNG image ---------------------------------------------------------------------------------------------
        // Reload entity from the database, to make sure it persisted
        final LocalMetaData testImagePngDb = db.find(LocalMetaData.class, testImagePng.getId());
        assertNotNull(testImagePngDb);

        // Assert a hash exists
        assertNotNull(testImagePngDb.getHash());

        // Assert the correct contrast
        assertEquals(Contrast.DARK, testImagePngDb.getHash().getContrast());

        // Assert the hashed bits are correct
        final byte[] testImagePngHashBits = Files.readAllBytes(testImagesDirectory.resolve("test-image.png.hash.bin"));
        assertArrayEquals(testImagePngHashBits, testImagePngDb.getHash().getBits());

        // Check GIF (still) image -------------------------------------------------------------------------------------
        // Reload entity from the database, to make sure it persisted
        final LocalMetaData testImageGifStillDb = db.find(LocalMetaData.class, testImageGifStill.getId());
        assertNotNull(testImageGifStillDb);

        // Assert a hash exists
        assertNotNull(testImageGifStillDb.getHash());

        // Assert the correct contrast
        assertEquals(Contrast.DARK, testImageGifStillDb.getHash().getContrast());

        // Assert the hashed bits are correct
        final byte[] testImageGifStillHashBits = Files.readAllBytes(testImagesDirectory.resolve("test-image-still.gif.hash.bin"));
        assertArrayEquals(testImageGifStillHashBits, testImageGifStillDb.getHash().getBits());

        // Check GIF (framed) image ------------------------------------------------------------------------------------
        // Reload entity from the database, to make sure it persisted
        final LocalMetaData testImageGifFramedDb = db.find(LocalMetaData.class, testImageGifFramed.getId());
        assertNotNull(testImageGifFramedDb);

        // Assert a hash exists
        assertNotNull(testImageGifFramedDb.getHash());

        // Assert the correct contrast
        assertEquals(Contrast.DARK, testImageGifFramedDb.getHash().getContrast());

        // Assert the hashed bits are correct
        final byte[] testImageGifFramedBits = Files.readAllBytes(testImagesDirectory.resolve("test-image-framed.gif.hash.bin"));
        assertArrayEquals(testImageGifFramedBits, testImageGifFramedDb.getHash().getBits());
    }
}
