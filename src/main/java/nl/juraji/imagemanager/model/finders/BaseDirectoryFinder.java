package nl.juraji.imagemanager.model.finders;

import io.ebean.Finder;
import nl.juraji.imagemanager.model.domain.BaseDirectory;

import java.util.List;
import java.util.UUID;

/**
 * Created by Juraji on 25-11-2018.
 * Image Manager 2
 */
public abstract class BaseDirectoryFinder<T extends BaseDirectory> extends Finder<UUID, T> {
    protected BaseDirectoryFinder(Class<T> type) {
        super(type);
    }

    public List<T> rootDirectories() {
        return this.query()
                .where()
                .isNull("parent")
                .orderBy("name")
                .findList();
    }
}
