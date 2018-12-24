package nl.juraji.imagemanager.tasks.pinterest;

import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinMetaData;
import nl.juraji.imagemanager.model.web.pinterest.resources.pins.DeletePinResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.pins.DeletePinResourceResult;
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
    protected BaseMetaData call() throws Exception {
        this.init();

        final DeletePinResourceRequest request = new DeletePinResourceRequest(metaData.getPinId(), getCSRFToken());
        final DeletePinResourceResult result = executeResourceRequest(request);

        if (result.getStatus() != 200) {
            throw new ResourceRequestFailedException(request, result);
        }

        final DeleteMetaDataTask deleteMetaDataTask = new DeleteMetaDataTask(metaData, true);
        deleteMetaDataTask.run();
        return deleteMetaDataTask.get();
    }
}
