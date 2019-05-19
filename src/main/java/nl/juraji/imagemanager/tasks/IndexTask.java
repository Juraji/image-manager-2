package nl.juraji.imagemanager.tasks;

import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.pivot.dialogs.TaskDialog;
import nl.juraji.imagemanager.tasks.pinterest.DownloadPinterestBoardTask;
import nl.juraji.imagemanager.tasks.pinterest.IndexBoardPinsFeedTask;
import nl.juraji.imagemanager.tasks.pinterest.IndexSectionPinsFeedTask;

import java.util.Set;

/**
 * Created by Juraji on 18-5-2019.
 * image-manager
 */
public final class IndexTask {
    private final TaskDialog taskDialog;
    private final boolean doReindex;

    private IndexTask(TaskDialog taskDialog, boolean doReindex) {
        // Private constructor
        this.taskDialog = taskDialog;
        this.doReindex = doReindex;
    }

    public static IndexTask inDialog(TaskDialog taskDialog, boolean doReindex) {
        return new IndexTask(taskDialog, doReindex);
    }

    public void addIndexTask(BaseDirectory directory) {
        if (directory instanceof LocalDirectory) {
            taskDialog.thenRun(new IndexLocalDirectoryTask((LocalDirectory) directory));
        } else if (directory instanceof PinterestBoard) {
            final PinterestBoard board = (PinterestBoard) directory;
            taskDialog
                    .thenRun(new IndexBoardPinsFeedTask(board, doReindex))
                    .thenRun(new DownloadPinterestBoardTask(board));


            final Set<PinterestBoard> children = board.getChildren();
            if (!children.isEmpty()) {
                children.forEach(child -> taskDialog
                        .thenRun(new IndexSectionPinsFeedTask(child, doReindex))
                        .thenRun(new DownloadPinterestBoardTask(child)));
            }
        }

        taskDialog
                .thenRun(new CorrectFileTypesTask(directory))
                .thenRun(new HashDirectoryTask(directory));
    }
}
