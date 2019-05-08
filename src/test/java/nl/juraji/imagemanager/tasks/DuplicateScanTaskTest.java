package nl.juraji.imagemanager.tasks;

import nl.juraji.imagemanager.fxml.controls.DuplicateSet;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalMetaData;
import org.junit.jupiter.api.Test;
import util.JavaFXAndEbeanBootstrappedTest;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Juraji on 8-5-2019.
 * image-manager
 */
class DuplicateScanTaskTest extends JavaFXAndEbeanBootstrappedTest {
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
        assertDuplicateSetHasImages(
                duplicateSets.get(0),
                "gif-animated-image.gif",
                "gif-animated-image2.gif"
        );

        // Second set should be the jpg, png and still gif images
        assertDuplicateSetHasImages(
                duplicateSets.get(1),
                "gif-still-image.gif",
                "jpg-image.jpg",
                "png-image.png"
        );

        // Third set should be the webp images
        assertDuplicateSetHasImages(
                duplicateSets.get(2),
                "webp-image.webp",
                "webp-image2.webp"
        );
    }

    private void assertDuplicateSetHasImages(DuplicateSet set, String... names) {
        List<BaseMetaData> duplicates = set.getDuplicates();
        assertEquals(names.length, duplicates.size());

        List<String> imageNames = duplicates.stream()
                .map(BaseMetaData::getComments)
                .collect(Collectors.toList());

        for (String name : names) {
            assertTrue(imageNames.contains(name));
        }
    }
}
