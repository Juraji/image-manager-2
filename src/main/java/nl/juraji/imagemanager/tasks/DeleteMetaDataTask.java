package nl.juraji.imagemanager.tasks;

import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTask;

/**
 * Created by Juraji on 9-12-2018.
 * Image Manager 2
 */
public class DeleteMetaDataTask extends ManagerTask<BaseMetaData> {
    private final BaseMetaData metaData;
    private final boolean deleteFile;

    public DeleteMetaDataTask(BaseMetaData metaData, boolean deleteFile) {
        super("Deleting meta data %s", metaData.getPath().getFileName().toString());
        this.metaData = metaData;
        this.deleteFile = deleteFile;
    }

    @Override
    public BaseMetaData call() throws Exception {
        if (deleteFile) {
            FileUtils.delete(metaData.getPath());
        }

        metaData.delete();
        return metaData;
    }
}
