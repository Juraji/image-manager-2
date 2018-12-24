package nl.juraji.imagemanager.tasks;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import net.sf.jmimemagic.*;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.StringUtils;
import nl.juraji.imagemanager.util.fxml.concurrent.IndicatorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 26-11-2018.
 * Image Manager 2
 */
public class CorrectFileTypesTask extends IndicatorTask<Void> {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final BaseDirectory directory;
    private final EbeanServer db;

    public CorrectFileTypesTask(BaseDirectory directory) {
        super("Correcting file types");
        this.directory = directory;
        this.db = Ebean.getDefaultServer();
    }

    @Override
    protected Void call() {
        setTotalWork(directory.getTotalMetaDataCount());
        correctFileTypes(directory);
        return null;
    }

    private void updateMessage(BaseDirectory d) {
        super.updateMessage("Correcting file types for %s", d.getName());
    }

    @SuppressWarnings("unchecked")
    private void correctFileTypes(BaseDirectory parent) {
        this.checkCanceled();
        updateMessage(parent);

        // Correct files not previously corrected
        final Set<BaseMetaData> correctedMetaData = ((Set<BaseMetaData>) parent.getMetaData()).stream()
                .filter(m -> !m.isFileCorrected())
                .peek(m -> this.checkCanceled())
                .map(this::correctFileTypeFor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Persist changes
        this.db.updateAll(correctedMetaData);

        // Check child directories
        final Set<BaseDirectory> children = parent.getChildren();
        for (BaseDirectory child : children) {
            this.correctFileTypes(child);
        }
    }

    private BaseMetaData correctFileTypeFor(BaseMetaData item) {
        final Path originalPath = item.getPath();

        if (FileUtils.exists(originalPath)) {
            final Path matchedPath = this.getMagicMatchedFile(originalPath);

            try {
                // #getMagicMatchedFile will return a new File object when the file should be moved
                if (!originalPath.equals(matchedPath)) {
                    final Path originalName = originalPath.getName(-1);
                    final Path matchedPathName = matchedPath.getName(-1);

                    logger.info("Incorrect file type: {} should be {}", originalName, matchedPathName);
                    if (FileUtils.exists(matchedPath)) {
                        logger.info("{} already exists, deleting {}", matchedPath, originalPath);
                        // matched file already exists, delete the incorrect one
                        Files.deleteIfExists(originalPath);
                    } else {
                        // Move the original file to the matchedFile location
                        Files.move(originalPath, matchedPath);
                    }

                    item.setPath(matchedPath);
                }

                item.setFileCorrected(true);
                return item;
            } catch (IOException e) {
                logger.warn("Error correcting file type for " + originalPath, e);
            }
        }

        return null;
    }

    private Path getMagicMatchedFile(Path path) {
        try {
            final String orgExtension = FileUtils.getFileExtension(path);
            final MagicMatch magicMatch = Magic.getMagicMatch(path.toFile(), false, true);
            final String magicMatchExtension = magicMatch.getExtension();

            if (StringUtils.isNotEmpty(magicMatchExtension)) {
                if (!magicMatchExtension.equals(orgExtension)) {
                    path = Paths.get(path.getFileName().toString().replace(orgExtension, magicMatchExtension));
                }
            }
        } catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
            // If file type can't be inferred there's nothing we can do
        }

        return path;
    }
}
