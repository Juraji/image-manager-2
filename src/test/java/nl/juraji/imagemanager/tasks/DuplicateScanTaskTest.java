package nl.juraji.imagemanager.tasks;

import nl.juraji.imagemanager.fxml.controls.DuplicateSet;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalMetaData;
import org.junit.Test;
import util.EbeanTest;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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

        // List all test images and add them to the test directory
        final ArrayList<String> testImages = new ArrayList<>();
        testImages.add("gif-animated-image.gif");
        testImages.add("gif-animated-image2.gif");
        testImages.add("gif-still-image.gif");
        testImages.add("jpg-image.jpg");
        testImages.add("png-image.png");
        testImages.add("webp-image.webp");
        testImages.add("webp-image2.webp");

        testImages.forEach(imageName -> {
            // Add the image as metadata
            final LocalMetaData metaData = new LocalMetaData();
            metaData.setDirectory(directory);
            metaData.setPath(testImagesDirectory.resolve(imageName));
            metaData.setComments(imageName);
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
