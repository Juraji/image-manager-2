package nl.juraji.imagemanager.tasks.pinterest;

import nl.juraji.imagemanager.model.domain.pinterest.BoardType;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.model.finders.PinterestBoardsFinder;
import nl.juraji.imagemanager.model.finders.SettingsFinder;
import nl.juraji.imagemanager.model.web.pinterest.resources.boards.BoardsResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.boards.BoardsResourceResult;
import nl.juraji.imagemanager.model.web.pinterest.types.BoardResource;
import nl.juraji.imagemanager.util.StringUtils;
import nl.juraji.imagemanager.util.URIUtils;

import java.net.URI;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 27-11-2018.
 * Image Manager 2
 */
public class FetchPinterestBoardsTask extends PinterestWebTask<List<PinterestBoard>> {
    private final Path defaultTargetDirectory;

    public FetchPinterestBoardsTask() {
        super("Fetching Pinterest boards...");
        this.defaultTargetDirectory = SettingsFinder.getSettings().getDefaultTargetDirectory();
    }

    @Override
    public List<PinterestBoard> call() throws Exception {
        super.init();

        final BoardsResourceRequest request = new BoardsResourceRequest(getInitialState().getViewer().getUsername());
        final BoardsResourceResult result = executeResourceRequest(request);

        this.checkIsCanceled();
        return result.getData().stream()
                .map(this::mapBoardResource)
                .sorted(Comparator.comparing(PinterestBoard::getName))
                .collect(Collectors.toList());
    }

    private PinterestBoard mapBoardResource(BoardResource boardResource) {
        final Optional<PinterestBoard> existingBoardOpt = PinterestBoardsFinder.find().findByBoardId(boardResource.getId());
        final PinterestBoard board = existingBoardOpt.orElseGet(PinterestBoard::new);

        final String name = boardResource.getName();

        // If is new boards set board type and target directory
        if (!existingBoardOpt.isPresent()) {
            board.setType(BoardType.BOARD);

            final String fileNameSafe = StringUtils.fileNameSafe(name);
            board.setLocationOnDisk(defaultTargetDirectory.resolve(fileNameSafe));
        }

        // Update board info
        final URI boardUri = PINTEREST_BASE_URI.resolve(boardResource.getUrl());
        final String slug = URIUtils.getPathSection(boardUri, -1);

        board.setName(name);
        board.setBoardId(boardResource.getId());
        board.setBoardUrl(boardUri);
        board.setSlug(slug);

        return board;
    }
}
