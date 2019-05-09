package nl.juraji.imagemanager.tasks;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import nl.juraji.imagemanager.model.domain.hashes.Contrast;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalMetaData;
import org.junit.Test;
import util.EbeanTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by Juraji on 7-5-2019.
 * image-manager
 */

public class HashDirectoryTaskTest extends EbeanTest {
    private final Path testImagesDirectory;
    private final EbeanServer db;

    public HashDirectoryTaskTest() throws URISyntaxException {
        this.testImagesDirectory = Paths.get(
                HashDirectoryTask.class.getResource("/test-images").toURI());
        this.db = Ebean.getDefaultServer();
    }

    @Test
    public void testHashingGif() throws IOException {
        // Setup test directory
        final LocalDirectory directory = new LocalDirectory();
        directory.setName("Test GIF directory");
        directory.setLocationOnDisk(testImagesDirectory);

        final LocalMetaData metaData = new LocalMetaData();
        metaData.setDirectory(directory);
        metaData.setPath(testImagesDirectory.resolve("gif-still-image.gif"));

        // Task expects directory to be persisted in the database in order to update it
        directory.getMetaData().add(metaData);
        directory.save();

        // Call hashing task
        final HashDirectoryTask hashDirectoryTask = new HashDirectoryTask(directory);
        hashDirectoryTask.call();

        // Check hashes
        // Reload entity from the database, to make sure it persisted
        final LocalMetaData metaDataFromDb = db.find(LocalMetaData.class, metaData.getId());
        assertNotNull(metaDataFromDb);

        // Check image properties
        assertEquals(448, metaDataFromDb.getWidth());
        assertEquals(340, metaDataFromDb.getHeight());
        assertEquals(92767, metaDataFromDb.getFileSize());

        // Assert a hash exists
        assertNotNull(metaDataFromDb.getHash());

        // Assert the correct contrast
        assertEquals(Contrast.DARK, metaDataFromDb.getHash().getContrast());

        // Assert the hashed bits are correct
        final byte[] imageBits = Files.readAllBytes(testImagesDirectory.resolve("gif-still-image.gif.hash.bin"));
        assertArrayEquals(imageBits, metaDataFromDb.getHash().getBits());
    }

    @Test
    public void testHashingJpg() throws IOException {
        // Setup test directory
        final LocalDirectory directory = new LocalDirectory();
        directory.setName("Test JPG directory");
        directory.setLocationOnDisk(testImagesDirectory);

        final LocalMetaData metaData = new LocalMetaData();
        metaData.setDirectory(directory);
        metaData.setPath(testImagesDirectory.resolve("jpg-image.jpg"));

        // Task expects directory to be persisted in the database in order to update it
        directory.getMetaData().add(metaData);
        directory.save();

        // Call hashing task
        final HashDirectoryTask hashDirectoryTask = new HashDirectoryTask(directory);
        hashDirectoryTask.call();

        // Check hashes
        // Reload entity from the database, to make sure it persisted
        final LocalMetaData metaDataFromDb = db.find(LocalMetaData.class, metaData.getId());
        assertNotNull(metaDataFromDb);

        // Check image properties
        assertEquals(448, metaDataFromDb.getWidth());
        assertEquals(340, metaDataFromDb.getHeight());
        assertEquals(36394, metaDataFromDb.getFileSize());

        // Assert a hash exists
        assertNotNull(metaDataFromDb.getHash());

        // Assert the correct contrast
        assertEquals(Contrast.DARK, metaDataFromDb.getHash().getContrast());

        // Assert the hashed bits are correct
        final byte[] imageBits = Files.readAllBytes(testImagesDirectory.resolve("jpg-image.jpg.hash.bin"));
        assertArrayEquals(imageBits, metaDataFromDb.getHash().getBits());
    }

    @Test
    public void testHashingPng() throws IOException {
        // Setup test directory
        final LocalDirectory directory = new LocalDirectory();
        directory.setName("Test PNG directory");
        directory.setLocationOnDisk(testImagesDirectory);

        final LocalMetaData metaData = new LocalMetaData();
        metaData.setDirectory(directory);
        metaData.setPath(testImagesDirectory.resolve("png-image.png"));

        // Task expects directory to be persisted in the database in order to update it
        directory.getMetaData().add(metaData);
        directory.save();

        // Call hashing task
        final HashDirectoryTask hashDirectoryTask = new HashDirectoryTask(directory);
        hashDirectoryTask.call();

        // Check hashes
        // Reload entity from the database, to make sure it persisted
        final LocalMetaData metaDataFromDb = db.find(LocalMetaData.class, metaData.getId());
        assertNotNull(metaDataFromDb);

        // Check image properties
        assertEquals(448, metaDataFromDb.getWidth());
        assertEquals(340, metaDataFromDb.getHeight());
        assertEquals(188933, metaDataFromDb.getFileSize());

        // Assert a hash exists
        assertNotNull(metaDataFromDb.getHash());

        // Assert the correct contrast
        assertEquals(Contrast.DARK, metaDataFromDb.getHash().getContrast());

        // Assert the hashed bits are correct
        final byte[] imageBits = Files.readAllBytes(testImagesDirectory.resolve("png-image.png.hash.bin"));
        assertArrayEquals(imageBits, metaDataFromDb.getHash().getBits());
    }

    @Test
    public void testHashingAnimatedGif() throws IOException {
        // Setup test directory
        final LocalDirectory directory = new LocalDirectory();
        directory.setName("Test Animated GIF directory");
        directory.setLocationOnDisk(testImagesDirectory);

        final LocalMetaData metaData = new LocalMetaData();
        metaData.setDirectory(directory);
        metaData.setPath(testImagesDirectory.resolve("gif-animated-image.gif"));

        // Task expects directory to be persisted in the database in order to update it
        directory.getMetaData().add(metaData);
        directory.save();

        // Call hashing task
        final HashDirectoryTask hashDirectoryTask = new HashDirectoryTask(directory);
        hashDirectoryTask.call();

        // Check hashes
        // Reload entity from the database, to make sure it persisted
        final LocalMetaData metaDataFromDb = db.find(LocalMetaData.class, metaData.getId());
        assertNotNull(metaDataFromDb);

        // Check image properties
        assertEquals(265, metaDataFromDb.getWidth());
        assertEquals(199, metaDataFromDb.getHeight());
        assertEquals(264749, metaDataFromDb.getFileSize());

        // Assert a hash exists
        assertNotNull(metaDataFromDb.getHash());

        // Assert the correct contrast
        assertEquals(Contrast.DARK, metaDataFromDb.getHash().getContrast());

        // Assert the hashed bits are correct
        final byte[] imageBits = Files.readAllBytes(testImagesDirectory.resolve("gif-animated-image.gif.hash.bin"));
        assertArrayEquals(imageBits, metaDataFromDb.getHash().getBits());
    }

    @Test
    public void testHashingWebP() throws IOException {
        // Setup test directory
        final LocalDirectory directory = new LocalDirectory();
        directory.setName("Test WebP directory");
        directory.setLocationOnDisk(testImagesDirectory);

        final LocalMetaData metaData = new LocalMetaData();
        metaData.setDirectory(directory);
        metaData.setPath(testImagesDirectory.resolve("webp-image.webp"));

        // Task expects directory to be persisted in the database in order to update it
        directory.getMetaData().add(metaData);
        directory.save();

        // Call hashing task
        final HashDirectoryTask hashDirectoryTask = new HashDirectoryTask(directory);
        hashDirectoryTask.call();

        // Check hashes
        // Reload entity from the database, to make sure it persisted
        final LocalMetaData metaDataFromDb = db.find(LocalMetaData.class, metaData.getId());
        assertNotNull(metaDataFromDb);

        // Check image properties
        assertEquals(550, metaDataFromDb.getWidth());
        assertEquals(368, metaDataFromDb.getHeight());
        assertEquals(30320, metaDataFromDb.getFileSize());

        // Assert a hash exists
        assertNotNull(metaDataFromDb.getHash());

        // Assert the correct contrast
        assertEquals(Contrast.DARK, metaDataFromDb.getHash().getContrast());

        // Assert the hashed bits are correct
        final byte[] imageBits = Files.readAllBytes(testImagesDirectory.resolve("webp-image.webp.hash.bin"));
        assertArrayEquals(imageBits, metaDataFromDb.getHash().getBits());
    }
}
