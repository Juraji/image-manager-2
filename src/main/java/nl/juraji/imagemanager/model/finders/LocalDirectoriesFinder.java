package nl.juraji.imagemanager.model.finders;

import nl.juraji.imagemanager.model.domain.local.LocalDirectory;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by Juraji on 3-12-2018.
 * Image Manager 2
 */
public class LocalDirectoriesFinder extends BaseDirectoryFinder<LocalDirectory> {
    private static final LocalDirectoriesFinder INSTANCE = new LocalDirectoriesFinder();

    private LocalDirectoriesFinder() {
        super(LocalDirectory.class);
    }

    public static LocalDirectoriesFinder find() {
        return INSTANCE;
    }

    public Optional<LocalDirectory> byLocationOnDisk(Path path) {
        return this.query().where()
                .eq("locationOnDisk", path)
                .findOneOrEmpty();
    }
}
