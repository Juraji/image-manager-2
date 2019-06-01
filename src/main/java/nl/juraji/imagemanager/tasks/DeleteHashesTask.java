package nl.juraji.imagemanager.tasks;

import io.ebean.Model;
import nl.juraji.imagemanager.model.domain.local.Directory;
import nl.juraji.imagemanager.model.domain.local.MetaData;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTask;

import java.util.Objects;
import java.util.Set;

/**
 * Created by Juraji on 15-12-2018.
 * Image Manager 2
 */
public class DeleteHashesTask extends ManagerTask<Void> {
    private final Directory directory;

    public DeleteHashesTask(Directory directory) {
        super("Deleting hashes");
        this.directory = directory;
    }

    @Override
    public Void call() {
        this.deleteHashes(directory);
        return null;
    }

    private void deleteHashes(Directory parent) {
        updateTaskDescription("Deleting hashes for %s", directory.getName());

        final Set<MetaData> metaData = parent.getMetaData();
        addWorkTodo(metaData.size());

        metaData.stream()
                .map(m -> {
                    this.checkIsCanceled();
                    return m.getHash();
                })
                .filter(Objects::nonNull)
                .forEach(Model::delete);

        parent.getChildren().forEach(this::deleteHashes);
    }
}
