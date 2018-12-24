package nl.juraji.imagemanager.tasks.pinterest;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import nl.juraji.imagemanager.model.domain.pinterest.PinMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.StringUtils;
import nl.juraji.imagemanager.util.fxml.concurrent.IndicatorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 26-11-2018.
 * Image Manager 2
 */
public class DownloadPinterestBoardTask extends IndicatorTask<Void> {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final PinterestBoard board;
    private final EbeanServer db;

    public DownloadPinterestBoardTask(PinterestBoard board) {
        super("Downloading images");
        this.board = board;
        this.db = Ebean.getDefaultServer();
    }

    @Override
    protected Void call() throws Exception {
        this.downloadPins(board);
        return null;
    }

    private void updateMessage(PinterestBoard b) {
        this.updateMessage("Downloading images for %s %s", b.getType().toString(), b.getName());
    }

    private void downloadPins(PinterestBoard parent) throws IOException {
        updateMessage(parent);

        final Path boardLocationOnDisk = parent.getLocationOnDisk();
        if (!FileUtils.exists(boardLocationOnDisk)) {
            Files.createDirectories(boardLocationOnDisk);
            logger.info("Created directory for {}: {}", parent.getName(), boardLocationOnDisk);
        }

        final Set<PinMetaData> pinsToDownload = parent.getMetaData().stream()
                .filter(m -> m.getPath() == null)
                .collect(Collectors.toSet());

        addToTotalWork(pinsToDownload.size());

        pinsToDownload.parallelStream()
                .peek(p -> this.checkCanceled())
                .peek(p -> incrementProgress())
                .forEach(m -> this.downloadPin(boardLocationOnDisk, m));

        this.checkCanceled();
        final Set<PinterestBoard> boardChildren = parent.getChildren();
        if (boardChildren.size() > 0) {
            for (PinterestBoard child : boardChildren) {
                this.downloadPins(child);
            }
        }

        db.updateAll(pinsToDownload);
    }

    private void downloadPin(Path baseLocationOnDisk, PinMetaData pinMetaData) {
        final URI downloadUrl = pinMetaData.getDownloadUrl();
        final Path targetPath = createTargetPath(baseLocationOnDisk, pinMetaData);

        if (FileUtils.exists(targetPath)) {
            pinMetaData.setPath(targetPath);
        } else {
            logger.info("Downloading pin: {} to {}", downloadUrl, targetPath);
            try {
                try (InputStream input = downloadUrl.toURL().openStream()) {
                    Files.copy(input, targetPath);
                }

                pinMetaData.setPath(targetPath);
            } catch (IOException e) {
                logger.warn("Failed downloading pin from {}: {}", downloadUrl, e.getMessage());
            }
        }
    }

    private Path createTargetPath(Path basePath, PinMetaData pinMetaData) {
        final String fileExtension = FileUtils.getFileExtension(pinMetaData.getDownloadUrl().toString());
        final StringBuilder builder = new StringBuilder(pinMetaData.getPinId());

        if (StringUtils.isNotEmpty(pinMetaData.getTitle())) {
            final String safeTitle = StringUtils.fileNameSafe(pinMetaData.getTitle());
            builder.append(" - ").append(safeTitle);
        }

        if (StringUtils.isNotEmpty(pinMetaData.getComments())) {
            String safeComments = StringUtils.fileNameSafe(pinMetaData.getComments());
            builder.append(" - ").append(safeComments);
        }

        builder.append('.').append(fileExtension);

        return basePath.resolve(builder.toString());
    }
}
