package nl.juraji.imagemanager.tasks;

import nl.juraji.imagemanager.model.domain.local.MetaData;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTask;

/**
 * Created by Juraji on 9-12-2018.
 * Image Manager 2
 */
public class DeleteMetaDataTask extends ManagerTask<MetaData> {
    private final MetaData metaData;
    private final boolean deleteFile;

    public DeleteMetaDataTask(MetaData metaData, boolean deleteFile) {
        super("Deleting meta data %s", metaData.getPath().getFileName().toString());
        this.metaData = metaData;
        this.deleteFile = deleteFile;
    }

    @Override
    public MetaData call() throws Exception {
        if (deleteFile) {
            FileUtils.delete(metaData.getPath());
        }

        metaData.delete();
        return metaData;
    }
}
