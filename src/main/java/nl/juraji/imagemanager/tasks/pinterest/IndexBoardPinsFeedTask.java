package nl.juraji.imagemanager.tasks.pinterest;

import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.model.web.pinterest.resources.boards.BoardPageResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.boards.BoardPageResourceResult;
import nl.juraji.imagemanager.model.web.pinterest.resources.boards.BoardPinsResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.boards.BoardPinsResourceResult;

import java.io.IOException;

/**
 * Created by Juraji on 4-12-2018.
 * Image Manager 2
 */
public class IndexBoardPinsFeedTask extends IndexPinsFeedTask {

    public IndexBoardPinsFeedTask(PinterestBoard board, boolean doReindex) {
        super(board, doReindex);
    }

    @Override
    protected BoardPinsResourceResult getPinsFeed(String boardId, String bookmark) throws IOException {
        return executeResourceRequest(new BoardPinsResourceRequest(boardId, bookmark));
    }

    @Override
    protected int getReportedPinCount() throws IOException {
        final BoardPageResourceRequest boardPageResourceRequest = new BoardPageResourceRequest(
                getInitialState().getViewer().getUsername(),
                getBoard().getSlug());

        final BoardPageResourceResult result = executeResourceRequest(boardPageResourceRequest);
        return result.getData().getSectionlessPinCount();
    }
}
