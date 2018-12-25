package nl.juraji.imagemanager.tasks;

import javafx.stage.Window;
import nl.juraji.imagemanager.fxml.dialogs.WorkQueueDialog;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.pinterest.BoardType;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.tasks.pinterest.DownloadPinterestBoardTask;
import nl.juraji.imagemanager.tasks.pinterest.IndexBoardPinsFeedTask;
import nl.juraji.imagemanager.tasks.pinterest.IndexSectionPinsFeedTask;
import nl.juraji.imagemanager.util.fxml.OptionDialogBuilder;

import java.util.Collection;

/**
 * Created by Juraji on 29-11-2018.
 * Image Manager 2
 */
public final class IndexDirectoryTaskBuilder {
    private final WorkQueueDialog<Void> indicator;
    private final Window owner;
    private boolean doIndexDirectory = false;
    private boolean doOptionalDownload = false;
    private boolean doCorrectFileTypes = false;
    private boolean doGenerateHashes = false;
    private boolean doReindexPinterestBoard = false;

    private IndexDirectoryTaskBuilder(Window owner) {
        this.indicator = new WorkQueueDialog<>(owner);
        this.owner = owner;
    }

    public static IndexDirectoryTaskBuilder build(Window owner) {
        return new IndexDirectoryTaskBuilder(owner);
    }


    public static IndexDirectoryTaskBuilder standard(Window owner) {
        return build(owner)
                .withIndexItems()
                .withOptionalDownload()
                .withCorrectFileTypes()
                .withGenerateHashes();
    }

    public IndexDirectoryTaskBuilder withIndexItems() {
        this.doIndexDirectory = true;
        return this;
    }

    public IndexDirectoryTaskBuilder withOptionalDownload() {
        this.doOptionalDownload = true;
        return this;
    }

    public IndexDirectoryTaskBuilder withCorrectFileTypes() {
        this.doCorrectFileTypes = true;
        return this;
    }

    public IndexDirectoryTaskBuilder withGenerateHashes() {
        this.doGenerateHashes = true;
        return this;
    }

    public IndexDirectoryTaskBuilder afterEach(Runnable runnable) {
        indicator.addTaskEndNotification(o -> runnable.run());
        return this;
    }

    public IndexDirectoryTaskBuilder afterAll(Runnable runnable) {
        indicator.addQueueEndNotification(runnable);
        return this;
    }

    public void execute(Collection<BaseDirectory> directories) {
        final boolean hasPinterestBoards = directories.stream().anyMatch(directory -> directory instanceof PinterestBoard);

        if (hasPinterestBoards) {
            final int optionIndex = OptionDialogBuilder.build(owner)
                    .withTitle("Index Pinterest boards")
                    .withMessage("You have selected to index Pinterest boards or sections. " +
                            "Should I index only the latest new pins, " +
                            "or index the whole board for new pins?")
                    .withOption("Latest pins only (faster)")
                    .withOption("Check whole board")
                    .show();

            if (optionIndex == -1) {
                // -1 is canceled
                return;
            }

            this.doReindexPinterestBoard = optionIndex == 1;
        }

        directories.forEach(this::init);
        indicator.execute();
    }

    private void init(BaseDirectory directory) {
        if (doIndexDirectory) {
            // Index directory or Pinterest board
            if (directory instanceof LocalDirectory) {
                indicator.queue(new IndexLocalDirectoryTask((LocalDirectory) directory));
            } else if (directory instanceof PinterestBoard) {
                PinterestBoard board = (PinterestBoard) directory;

                if (BoardType.BOARD.equals(board.getType())) {
                    indicator.queue(new IndexBoardPinsFeedTask(board, doReindexPinterestBoard));

                    board.getChildren().forEach(section ->
                            indicator.queue(new IndexSectionPinsFeedTask(section, doReindexPinterestBoard)));
                } else {
                    indicator.queue(new IndexSectionPinsFeedTask(board, doReindexPinterestBoard));
                }
            }
        }

        if (doOptionalDownload) {
            if (directory instanceof PinterestBoard) {
                // Download Pin images
                indicator.queue(new DownloadPinterestBoardTask((PinterestBoard) directory));
            }
        }

        if (doCorrectFileTypes) {
            // Correct file types
            indicator.queue(new CorrectFileTypesTask(directory));
        }

        if (doGenerateHashes) {
            // Hash files
            indicator.queue(new HashDirectoryTask(directory));
        }
    }
}
