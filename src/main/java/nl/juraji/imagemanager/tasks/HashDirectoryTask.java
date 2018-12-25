package nl.juraji.imagemanager.tasks;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.hashes.Contrast;
import nl.juraji.imagemanager.model.domain.hashes.HashData;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.fxml.concurrent.IndicatorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.awt.RenderingHints.*;

/**
 * Created by Juraji on 26-11-2018.
 * Image Manager 2
 */
public class HashDirectoryTask extends IndicatorTask<Void> {
    public static final int SAMPLE_SIZE = 100;
    public static final int CONTRAST_BRIGHTNESS_THRESHOLD = 152;

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final BaseDirectory directory;

    public HashDirectoryTask(BaseDirectory directory) {
        super("Creating hashes");
        this.directory = directory;
    }

    @Override
    protected Void call() {
        this.buildHashes(directory);
        return null;
    }

    @SuppressWarnings("unchecked")
    private void buildHashes(BaseDirectory parent) {
        updateMessage("Creating hashes for: %s", parent.getName());

        final Set<BaseMetaData> parentMetaData = ((Set<BaseMetaData>) parent.getMetaData()).stream()
                .filter(m -> m.getHash() == null)
                .collect(Collectors.toSet());
        final int metaDataCount = parent.getMetaData().size();

        if (metaDataCount > 0) {
            addToTotalWork(metaDataCount);

            final List<HashData> hashResults = parentMetaData.parallelStream()
                    .peek(o -> this.checkCanceled())
                    .map(this::generate)
                    .peek(o -> incrementProgress())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Save generated hashes in single transaction
            final EbeanServer db = Ebean.getDefaultServer();
            db.saveAll(hashResults);
            db.updateAll(parentMetaData);
        }

        this.checkCanceled();
        parent.getChildren().forEach(o -> this.buildHashes((BaseDirectory) o));
    }

    private HashData generate(BaseMetaData metaData) {
        HashData hashData = null;
        final Path filePath = metaData.getPath();

        if (FileUtils.exists(filePath)) {
            logger.info("Generating hash for " + filePath);

            try {
                final File file = filePath.toFile();
                final BufferedImage image = getImage(filePath);

                if (image == null) {
                    throw new IOException("Failed reading file");
                }

                hashData = new HashData();
                final long qualityRating = calculateQualityRating(image, file);

                generateHash(image, hashData);

                metaData.setHash(hashData);
                metaData.setQualityRating(qualityRating);
                metaData.setWidth(image.getWidth());
                metaData.setHeight(image.getHeight());
                metaData.setFileSize(file.length());
            } catch (Throwable e) {
                logger.warn("Failed building hash for {}, {}", filePath, e.getMessage());
            }
        }

        return hashData;
    }

    private BufferedImage getImage(Path path) throws IOException {
        final String fileExtension = FileUtils.getFileExtension(path).toUpperCase();

        if ("GIF".equals(fileExtension)) {
            // Gif images are treated by first frame
            try (final ImageInputStream in = ImageIO.createImageInputStream(path)) {
                final ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
                reader.setInput(in);

                // Read first frame image
                final BufferedImage frame = reader.read(0);

                // Copy to ARGB image
                final BufferedImage hqFrame = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                hqFrame.getGraphics().drawImage(frame, 0, 0, null);

                return hqFrame;
            }
        } else {
            return ImageIO.read(path.toFile());
        }
    }

    private long calculateQualityRating(BufferedImage image, File originalFile) {
        return (image.getWidth() * image.getHeight()) + originalFile.length();
    }

    private void generateHash(BufferedImage image, HashData hashData) {
        // Crop the image to contain only the center square
        final int cropSize = Math.min(image.getWidth(), image.getHeight());
        final int cropStartX = Math.max((image.getWidth() - cropSize) / 2, 0);
        final int cropStartY = Math.max((image.getHeight() - cropSize) / 2, 0);
        final BufferedImage centerCropImage = image.getSubimage(cropStartX, cropStartY, cropSize, cropSize);

        // Scale the image x and y to SAMPLE_SIZE and draw it back on the image buffer at [0,0]
        final Graphics2D graphics = centerCropImage.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
        graphics.drawImage(centerCropImage, 0, 0, SAMPLE_SIZE, SAMPLE_SIZE, null);
        graphics.dispose();

        // The last column is ignored due to it
        // not having a next column for comparison
        final int scanXCount = SAMPLE_SIZE - 1;
        final int scanYCount = SAMPLE_SIZE;
        final int totalXY = scanXCount * scanYCount;
        final BitSet set = new BitSet(totalXY);
        int bitSetIteration = 0;
        long totalRGB = 0;

        for (int y = 0; y < scanYCount; y++) {
            for (int x = 0; x < scanXCount; x++) {
                int rgbA = centerCropImage.getRGB(x, y) & 255;
                int rgbB = centerCropImage.getRGB(x + 1, y) & 255;
                set.set(bitSetIteration, rgbA < rgbB);
                totalRGB += rgbA;
                ++bitSetIteration;
            }
        }

        final Contrast contrast = (totalRGB / totalXY) > CONTRAST_BRIGHTNESS_THRESHOLD ? Contrast.LIGHT : Contrast.DARK;
        hashData.setContrast(contrast);
        hashData.setBits(set.toByteArray());
    }
}
