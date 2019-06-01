package nl.juraji.imagemanager.tasks;

import nl.juraji.imagemanager.model.domain.local.Directory;
import nl.juraji.imagemanager.model.finders.DirectoryFinder;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Created by Juraji on 26-11-2018.
 * Image Manager 2
 */
public class ImportLocalDirectoryTask extends ManagerTask<Directory> {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final Path root;
    private final boolean recursive;

    public ImportLocalDirectoryTask(Path root, boolean recursive) {
        super(buildTaskName(root, recursive));
        this.root = root;
        this.recursive = recursive;
    }

    @Override
    public Directory call() {
        final Optional<Directory> existingDirOpt = DirectoryFinder.find()
                .byLocationOnDisk(root);

        if (existingDirOpt.isPresent()) {
            final Directory existingDir = existingDirOpt.get();
            this.importSubDirectories(root, existingDir);
            return null;
        } else {
            return this.importDirectory(root, null);
        }
    }

    private static String buildTaskName(Path path, boolean recursive) {
        return String.format("Importing local directory \"%s\"%s", path.toString(), (recursive ? " recursively" : ""));
    }

    private Directory importDirectory(Path path, Directory parent) {
        final String taskName = buildTaskName(path, recursive);
        logger.info(taskName);
        updateTaskDescription(taskName);

        final Directory newDirectory = new Directory();
        newDirectory.setName(path.getFileName().toString());
        newDirectory.setLocationOnDisk(path);

        if (parent != null) {
            newDirectory.setParent(parent);
        }

        newDirectory.save();
        this.importSubDirectories(path, newDirectory);
        newDirectory.refresh();

        return newDirectory;
    }

    private void importSubDirectories(Path path, Directory parent) {
        this. checkIsCanceled();
        if (recursive) {
            final List<Path> subDirectoryPaths = FileUtils.getDirectorySubDirectories(path);
            subDirectoryPaths.forEach(p -> this.importDirectory(p, parent));
        }
    }
}
