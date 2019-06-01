package nl.juraji.imagemanager.model.finders;

import io.ebean.Finder;
import nl.juraji.imagemanager.model.domain.local.Directory;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class DirectoryFinder extends Finder<UUID, Directory> {
    private static final DirectoryFinder INSTANCE = new DirectoryFinder();

    private DirectoryFinder() {
        super(Directory.class);
    }

    public static DirectoryFinder find() {
        return INSTANCE;
    }

    public Optional<Directory> byLocationOnDisk(Path path) {
        return this.query().where()
                .eq("locationOnDisk", path)
                .findOneOrEmpty();
    }

    public List<Directory> rootDirectories() {
        return this.query().where()
                .isNull("parent")
                .findList();
    }
}
