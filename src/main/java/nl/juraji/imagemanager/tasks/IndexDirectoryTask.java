package nl.juraji.imagemanager.tasks;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import nl.juraji.imagemanager.model.domain.local.Directory;
import nl.juraji.imagemanager.model.domain.local.MetaData;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Juraji on 25-11-2018.
 * Image Manager 2
 */
public class IndexDirectoryTask extends ManagerTask<Void> {
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "gif", "png", "bmp", "webp", "tiff");
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final Directory directory;
    private final EbeanServer db;

    public IndexDirectoryTask(Directory directory) {
        super("Indexing local directory");
        this.directory = directory;
        this.db = Ebean.getDefaultServer();
    }

    @Override
    public Void call() throws IOException {
        setWorkTodo(calculateTotalDirectoryCount(directory));
        this.indexAndPersist(directory);
        directory.save();
        return null;
    }

    private int calculateTotalDirectoryCount(Directory directory) {
        return directory.getChildren().size() + directory.getChildren()
                .stream()
                .mapToInt(this::calculateTotalDirectoryCount)
                .sum();
    }

    private void indexAndPersist(Directory parent) throws IOException {
        updateTaskDescription("Indexing local directory %s", parent.getName());
        logger.info("Indexing {}", parent.getLocationOnDisk());

        final List<Path> files = FileUtils.getDirectoryFiles(parent.getLocationOnDisk(), SUPPORTED_EXTENSIONS);
        final Set<MetaData> existingMetaData = parent.getMetaData();

        logger.info("Found {} supported files, {} currently indexed", files.size(), existingMetaData.size());
        List<MetaData> createdMetaData = new ArrayList<>();
        for (Path file : files) {
            final boolean nonExistent = existingMetaData.stream()
                    .map(MetaData::getPath)
                    .noneMatch(f -> f.equals(file));

            if (nonExistent) {
                final MetaData metaData = new MetaData();
                metaData.setPath(file);
                metaData.setFileSize(Files.size(file));
                metaData.setDirectory(parent);
                createdMetaData.add(metaData);
            }
        }

        this.db.saveAll(createdMetaData);
        logger.info("Persisted {} meta data objects to the database", createdMetaData.size());
        existingMetaData.addAll(createdMetaData);

        this.checkIsCanceled();
        for (Directory child : parent.getChildren()) {
            indexAndPersist(child);
        }

        incrementWorkDone();
    }
}
