package nl.juraji.imagemanager.model.finders;

import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;

import java.util.Optional;

/**
 * Created by Juraji on 3-12-2018.
 * Image Manager 2
 */
public class PinterestBoardsFinder extends BaseDirectoryFinder<PinterestBoard> {
    private static final PinterestBoardsFinder INSTANCE = new PinterestBoardsFinder();

    private PinterestBoardsFinder() {
        super(PinterestBoard.class);
    }

    public static PinterestBoardsFinder find() {
        return INSTANCE;
    }

    public Optional<PinterestBoard> findByBoardId(String boardId) {
        return this.query().where()
                .eq("boardId", boardId)
                .findOneOrEmpty();
    }
}
