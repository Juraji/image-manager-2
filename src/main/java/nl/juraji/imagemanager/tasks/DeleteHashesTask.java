package nl.juraji.imagemanager.tasks;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.HashData;
import nl.juraji.imagemanager.util.fxml.concurrent.IndicatorTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 15-12-2018.
 * Image Manager 2
 */
public class DeleteHashesTask extends IndicatorTask<Void> {
    private final BaseDirectory directory;
    private final EbeanServer db;

    public DeleteHashesTask(BaseDirectory directory) {
        super("Deleting hashes");
        this.directory = directory;
        this.db = Ebean.getDefaultServer();
    }

    @Override
    protected Void call() {
        this.deleteHashes(directory);
        return null;
    }

    @SuppressWarnings("unchecked")
    private void deleteHashes(BaseDirectory parent) {
        updateMessage("Deleting hashes for %s", directory.getName());

        final Set<BaseMetaData> metaData = parent.getMetaData();
        addToTotalWork(metaData.size());

        final List<HashData> deletedHashes = new ArrayList<>();
        final List<BaseMetaData> changedMetaData = metaData.stream()
                .peek(m -> incrementProgress())
                .filter(m -> m.getHash() != null)
                .peek(m -> deletedHashes.add(m.getHash()))
                .peek(m -> m.setHash(null))
                .collect(Collectors.toList());

        db.saveAll(changedMetaData);
        db.deleteAll(deletedHashes);

        parent.getChildren().forEach(o -> this.deleteHashes((BaseDirectory) o));
    }
}
