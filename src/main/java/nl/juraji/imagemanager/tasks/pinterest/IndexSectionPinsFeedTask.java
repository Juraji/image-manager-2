package nl.juraji.imagemanager.tasks.pinterest;

import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.model.web.pinterest.resources.sections.BoardSectionPinsResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.sections.BoardSectionPinsResourceResult;
import nl.juraji.imagemanager.model.web.pinterest.resources.sections.BoardSectionResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.sections.BoardSectionResourceResult;

import java.io.IOException;

/**
 * Created by Juraji on 4-12-2018.
 * Image Manager 2
 */
public class IndexSectionPinsFeedTask extends IndexPinsFeedTask {

    public IndexSectionPinsFeedTask(PinterestBoard board, boolean doReindex) {
        super(board, doReindex);
    }

    @Override
    protected BoardSectionPinsResourceResult getPinsFeed(String boardId, String bookmark) throws IOException {
        return executeResourceRequest(new BoardSectionPinsResourceRequest(boardId, bookmark));
    }

    @Override
    protected int getReportedPinCount() throws IOException {
        final PinterestBoard board = getBoard();
        final BoardSectionResourceRequest boardSectionResourceRequest = new BoardSectionResourceRequest(
                getInitialState().getViewer().getUsername(),
                board.getParent().getSlug(),
                board.getSlug());

        final BoardSectionResourceResult result = executeResourceRequest(boardSectionResourceRequest);
        return result.getData().getPinCount();
    }
}
