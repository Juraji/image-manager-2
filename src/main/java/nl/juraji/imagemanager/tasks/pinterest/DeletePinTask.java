package nl.juraji.imagemanager.tasks.pinterest;

import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinMetaData;
import nl.juraji.imagemanager.model.web.pinterest.resources.pins.DeletePinResourceRequest;
import nl.juraji.imagemanager.tasks.DeleteMetaDataTask;

/**
 * Created by Juraji on 5-12-2018.
 * Image Manager 2
 */
public class DeletePinTask extends PinterestWebTask<BaseMetaData> {
    private final PinMetaData metaData;

    public DeletePinTask(PinMetaData metaData) {
        super("Deleting pin: %s", metaData.getPinId());
        this.metaData = metaData;
    }

    @Override
    public BaseMetaData call() throws Exception {
        super.call();

        final DeletePinResourceRequest request = new DeletePinResourceRequest(metaData.getPinId(), getCSRFToken());
        executeResourceRequest(request);

        final DeleteMetaDataTask deleteMetaDataTask = new DeleteMetaDataTask(metaData, true);
        return deleteMetaDataTask.call();
    }
}
