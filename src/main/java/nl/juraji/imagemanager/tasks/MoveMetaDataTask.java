package nl.juraji.imagemanager.tasks;

import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.finders.BaseMetaDataFinder;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.fxml.concurrent.IndicatorTask;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by Juraji on 8-12-2018.
 * Image Manager 2
 */
public class MoveMetaDataTask extends IndicatorTask<BaseMetaData> {

    private final BaseMetaData sourceMetaData;
    private final BaseDirectory targetDirectory;

    public MoveMetaDataTask(BaseMetaData metaData, BaseDirectory target) {
        super("Moving file %s to %s", metaData.getId(), target.getName());
        this.sourceMetaData = metaData;
        this.targetDirectory = target;
    }

    @Override
    protected BaseMetaData call() throws Exception {
        final Path source = sourceMetaData.getPath();
        final Path target = targetDirectory.getLocationOnDisk().resolve(source.getFileName());

        if (!source.equals(target)) {
            if (!FileUtils.exists(targetDirectory.getLocationOnDisk())) {
                Files.createDirectories(targetDirectory.getLocationOnDisk());
            }

            // Move the actual file
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
            sourceMetaData.setPath(target);

            // Move metadata in db
            BaseMetaDataFinder.setParent(sourceMetaData, targetDirectory);
            sourceMetaData.save();
        }

        return sourceMetaData;
    }
}
