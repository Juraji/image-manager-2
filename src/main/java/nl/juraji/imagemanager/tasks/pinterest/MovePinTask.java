package nl.juraji.imagemanager.tasks.pinterest;

import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.model.web.pinterest.resources.pins.MovePinResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.pins.MovePinResourceResult;
import nl.juraji.imagemanager.tasks.MoveMetaDataTask;

/**
 * Created by Juraji on 6-12-2018.
 * Image Manager 2
 */
public class MovePinTask extends PinterestWebTask<BaseMetaData> {

    private final PinMetaData metaData;
    private final PinterestBoard targetBoard;

    public MovePinTask(PinMetaData metaData, PinterestBoard targetBoard) {
        super("Moving pin %s to board %s...", metaData.getPinId(), targetBoard.getName());
        this.metaData = metaData;
        this.targetBoard = targetBoard;
    }

    @Override
    protected BaseMetaData call() throws Exception {
        this.init();

        // Move pin on Pinterest
        final MovePinResourceRequest request = new MovePinResourceRequest(metaData, targetBoard, getCSRFToken());
        final MovePinResourceResult result = executeResourceRequest(request);

        if (result.getStatus() != 200) {
            throw new ResourceRequestFailedException(request, result);
        }

        // Update pin info
        metaData.setPinterestUri(targetBoard.getBoardUrl().resolve("/pin/" + metaData.getPinId()));

        // Move pin locally
        final MoveMetaDataTask moveMetaDataTask = new MoveMetaDataTask(metaData, targetBoard);
        moveMetaDataTask.run();

        return moveMetaDataTask.get();
    }
}
