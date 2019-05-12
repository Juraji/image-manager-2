package nl.juraji.imagemanager.tasks;

import javafx.stage.Window;
import nl.juraji.imagemanager.fxml.dialogs.WorkDialog;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.pinterest.BoardType;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.tasks.pinterest.DownloadPinterestBoardTask;
import nl.juraji.imagemanager.tasks.pinterest.IndexBoardPinsFeedTask;
import nl.juraji.imagemanager.tasks.pinterest.IndexSectionPinsFeedTask;
import nl.juraji.imagemanager.util.fxml.OptionDialogBuilder;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTaskChain;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * Created by Juraji on 29-11-2018.
 * Image Manager 2
 */
@Deprecated
public final class IndexDirectoryTaskBuilder extends ManagerTaskChain<BaseDirectory, Void> {
    private final Collection<BaseDirectory> directories;
    private final Window owner;
    private boolean doReindex = false;

    private IndexDirectoryTaskBuilder(Window owner, Collection<BaseDirectory> directories) {
        super(directories);
        this.owner = owner;
        this.directories = directories;
    }

    public static IndexDirectoryTaskBuilder build(Window owner, Collection<BaseDirectory> directories) {
        return new IndexDirectoryTaskBuilder(owner, directories);
    }


    public static IndexDirectoryTaskBuilder standard(Window owner, Collection<BaseDirectory> directories) {
        return build(owner, directories)
                .withIndexItems()
                .withOptionalDownload()
                .withCorrectFileTypes()
                .withGenerateHashes();
    }

    @Override
    public IndexDirectoryTaskBuilder afterAll(Runnable runnable) {
        super.afterAll(runnable);
        return this;
    }

    @Override
    public IndexDirectoryTaskBuilder afterEach(Consumer<Void> consumer) {
        super.afterEach(consumer);
        return this;
    }

    public IndexDirectoryTaskBuilder withIndexItems() {
        this.nextTask(directory -> {
            // Index directory or Pinterest board
            if (directory instanceof LocalDirectory) {
                return new IndexLocalDirectoryTask((LocalDirectory) directory);
            } else if (directory instanceof PinterestBoard) {
                PinterestBoard board = (PinterestBoard) directory;

                if (BoardType.BOARD.equals(board.getType())) {
                    return new ManagerTaskChain<PinterestBoard, Void>(Collections.singleton(board))
                            .nextTask(d -> new IndexBoardPinsFeedTask(d, doReindex))
                            .nextTask(d -> new ManagerTaskChain<PinterestBoard, Void>(d.getChildren())
                                    .nextTask(s -> new IndexSectionPinsFeedTask(s, doReindex)));

                } else {
                    return new IndexSectionPinsFeedTask(board, doReindex);
                }
            }

            throw new UnsupportedOperationException("Indexing of " + directory.getClass() + " not supported!");
        });
        return this;
    }

    public IndexDirectoryTaskBuilder withOptionalDownload() {
        this.nextTask(d -> {
            if (d instanceof PinterestBoard) {
                return new DownloadPinterestBoardTask((PinterestBoard) d);
            } else {
                return null;
            }
        });
        return this;
    }

    public IndexDirectoryTaskBuilder withCorrectFileTypes() {
        this.nextTask(CorrectFileTypesTask::new);
        return this;
    }

    public IndexDirectoryTaskBuilder withGenerateHashes() {
        this.nextTask(HashDirectoryTask::new);
        return this;
    }

    public void runIndex() {
        final boolean hasPinterestBoards = this.directories.stream()
                .anyMatch(directory -> directory instanceof PinterestBoard);

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

            doReindex = optionIndex == 1;
        }

        new WorkDialog<Void>(owner).exec(this);
    }
}
