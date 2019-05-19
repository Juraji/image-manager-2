package nl.juraji.imagemanager.tasks;

import io.ebean.Model;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.util.concurrent.ManagerTask;

import java.util.Objects;
import java.util.Set;

/**
 * Created by Juraji on 15-12-2018.
 * Image Manager 2
 */
public class DeleteHashesTask extends ManagerTask<Void> {
    private final BaseDirectory directory;

    public DeleteHashesTask(BaseDirectory directory) {
        super("Deleting hashes");
        this.directory = directory;
    }

    @Override
    public Void call() {
        this.deleteHashes(directory);
        return null;
    }

    @SuppressWarnings("unchecked")
    private void deleteHashes(BaseDirectory parent) {
        updateTaskDescription("Deleting hashes for %s", directory.getName());

        final Set<BaseMetaData> metaData = parent.getMetaData();
        addWorkTodo(metaData.size());

        metaData.stream()
                .map(m -> {
                    this.checkIsCanceled();
                    return m.getHash();
                })
                .filter(Objects::nonNull)
                .forEach(Model::delete);

        parent.getChildren().forEach(o -> this.deleteHashes((BaseDirectory) o));
    }
}
