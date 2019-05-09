package nl.juraji.imagemanager.tasks.pinterest;

import nl.juraji.imagemanager.model.domain.pinterest.BoardType;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.model.finders.PinterestBoardsFinder;
import nl.juraji.imagemanager.model.web.pinterest.resources.sections.BoardSectionsResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.sections.BoardSectionsResourceResult;
import nl.juraji.imagemanager.model.web.pinterest.types.BoardSectionResource;
import nl.juraji.imagemanager.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 3-12-2018.
 * Image Manager 2
 */
public class ImportPinterestBoardSectionsTask extends PinterestWebTask<Void> {
    private final PinterestBoard parentBoard;

    public ImportPinterestBoardSectionsTask(PinterestBoard parentBoard) {
        super("Fetching Pinterest sections for %s...", parentBoard.getName());
        this.parentBoard = parentBoard;
    }

    @Override
    public Void call() throws Exception {
        super.init();

        final BoardSectionsResourceResult result = executeResourceRequest(new BoardSectionsResourceRequest(parentBoard.getBoardId()));

        this.checkIsCanceled();
        final List<PinterestBoard> collect = result.getData().stream()
                .map(this::mapSectionResource)
                .collect(Collectors.toList());

        PinterestBoardsFinder.find().db().saveAll(collect);

        return null;
    }

    private PinterestBoard mapSectionResource(BoardSectionResource sectionResource) {
        final Optional<PinterestBoard> existingSectionOpt = PinterestBoardsFinder.find().findByBoardId(sectionResource.getId());
        final PinterestBoard section = existingSectionOpt.orElseGet(PinterestBoard::new);

        final String name = sectionResource.getTitle();
        final String fileNameSafe = StringUtils.fileNameSafe(name);

        // If is new section, set board type, target location and parent
        if (!existingSectionOpt.isPresent()) {
            section.setType(BoardType.SECTION);
            section.setLocationOnDisk(parentBoard.getLocationOnDisk().resolve(fileNameSafe));
            section.setParent(parentBoard);
        }

        // Update section info
        section.setName(name);
        section.setBoardId(sectionResource.getId());
        section.setBoardUrl(parentBoard.getBoardUrl().resolve(sectionResource.getSlug()));
        section.setSlug(sectionResource.getSlug());

        return section;
    }
}
