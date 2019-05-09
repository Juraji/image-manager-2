package nl.juraji.imagemanager.tasks;

import nl.juraji.imagemanager.fxml.controls.DuplicateSet;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalMetaData;
import org.junit.Test;
import util.EbeanTest;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Juraji on 8-5-2019.
 * image-manager
 */
public class DuplicateScanTaskTest extends EbeanTest {
    private final Path testImagesDirectory;

    public DuplicateScanTaskTest() throws URISyntaxException {
        this.testImagesDirectory = Paths.get(
                HashDirectoryTask.class.getResource("/test-images").toURI());
    }

    @Test
    public void testDuplicateDetection() {
        // Setup test directory
        final LocalDirectory directory = new LocalDirectory();
        directory.setName("Duplicate scan test");
        directory.setLocationOnDisk(testImagesDirectory);

        // Find all test images and add them to the test directory
        final File[] files = testImagesDirectory.toFile().listFiles((dir, name) -> !name.endsWith(".bin"));
        assertNotNull(files);

        Arrays.stream(files).forEach(file -> {
            // Add the image as metadata
            final LocalMetaData metaData = new LocalMetaData();
            metaData.setDirectory(directory);
            metaData.setPath(file.toPath());
            metaData.setComments(file.getName());
            directory.getMetaData().add(metaData);
        });

        // HashDirectoryTask expects directory to be persisted in the database in order to update it
        directory.save();

        // Call hashing task (Hashes are needed for the duplicate scanner)
        final HashDirectoryTask hashDirectoryTask = new HashDirectoryTask(directory);
        hashDirectoryTask.call();

        // Call Duplicate scanner task
        final DuplicateScanTask duplicateScanTask = new DuplicateScanTask(directory, 0.7);
        final List<DuplicateSet> duplicateSets = duplicateScanTask.call();

        assertEquals(3, duplicateSets.size());

        // First set should be the animated gifs
        final DuplicateSet set0 = duplicateSets.get(0);
        final List<BaseMetaData> set0Duplicates = set0.getDuplicates();
        assertEquals(2, set0Duplicates.size());

        assertEquals("gif-animated-image2.gif", set0Duplicates.get(0).getComments());
        assertEquals("gif-animated-image.gif", set0Duplicates.get(1).getComments());

        // Second set should be the jpg, png and still gif images
        final DuplicateSet set1 = duplicateSets.get(1);
        final List<BaseMetaData> set1Duplicates = set1.getDuplicates();
        assertEquals(3, set1Duplicates.size());

        assertEquals("jpg-image.jpg", set1Duplicates.get(0).getComments());
        assertEquals("png-image.png", set1Duplicates.get(1).getComments());
        assertEquals("gif-still-image.gif", set1Duplicates.get(2).getComments());

        // Third set should be the webp images
        final DuplicateSet set2 = duplicateSets.get(2);
        final List<BaseMetaData> set2Duplicates = set2.getDuplicates();
        assertEquals(2, set2Duplicates.size());

        assertEquals("webp-image2.webp", set2Duplicates.get(0).getComments());
        assertEquals("webp-image.webp", set2Duplicates.get(1).getComments());
    }
}
