package nl.juraji.imagemanager.pivot;

import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.pivot.controls.DirectoryTreeContextMenu;
import nl.juraji.imagemanager.pivot.controls.DirectoryTreeView;
import nl.juraji.imagemanager.pivot.dialogs.TaskDialog;
import nl.juraji.imagemanager.pivot.sheets.PinterestBoardsSheet;
import nl.juraji.imagemanager.pivot.sheets.SettingsSheet;
import nl.juraji.imagemanager.tasks.ImportLocalDirectoryTask;
import nl.juraji.imagemanager.tasks.IndexTask;
import nl.juraji.imagemanager.tasks.LoadRootDirectoriesTask;
import nl.juraji.imagemanager.tasks.pinterest.ImportPinterestBoardSectionsTask;
import nl.juraji.imagemanager.util.DesktopUtils;
import nl.juraji.imagemanager.util.io.resources.ImageCache;
import nl.juraji.imagemanager.util.pivot.ActionUtils;
import nl.juraji.imagemanager.util.pivot.Alerts;
import nl.juraji.imagemanager.util.pivot.BXMLLoader;
import nl.juraji.imagemanager.util.pivot.EDT;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.Window;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Juraji on 13-5-2019.
 * image-manager
 */
public class MainWindow extends Window implements Bindable {

    @BXML
    private DirectoryTreeView directoryTreeView;

    public MainWindow() {
        ActionUtils.putNamedAction("FileOpenSettings", this::openSettings);
        ActionUtils.putNamedAction("FileViewLogs", this::viewLogs);
        ActionUtils.putNamedAction("FileExit", () -> System.exit(0));
        ActionUtils.putNamedAction("DirectoriesAddLocalDirectory", this::addLocalDirectory);
        ActionUtils.putNamedAction("DirectoriesAddPinterestBoards", this::addPinterestBoards);
        ActionUtils.putNamedAction("DirectoriesIndexAllDirectories", this::indexAllDirectories);
        ActionUtils.putNamedAction("ToolsDuplicateScanner", this::openDuplicateScanner);
    }

    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
        this.setIcon(ImageCache.getApplicationIconUrl());

        final DirectoryTreeContextMenu directoryTreeContextMenu = new DirectoryTreeContextMenu(this, this.directoryTreeView);
        this.directoryTreeView.setMenuHandler(directoryTreeContextMenu);

        // Run later, when display is initialized
        EDT.run(this::populateDirectoryTree);
    }

    private void openSettings() {
        final Sheet dialog = BXMLLoader.load(SettingsSheet.class);
        dialog.open(MainWindow.this);
    }

    private void viewLogs() {
        DesktopUtils.openFile(Paths.get("./logs"));
    }

    private void addLocalDirectory() {
        final FileBrowserSheet fileBrowserSheet = new FileBrowserSheet();
        fileBrowserSheet.setMode(FileBrowserSheet.Mode.SAVE_TO);

        fileBrowserSheet.open(this, sheet -> {
            final File directoryFile = fileBrowserSheet.getSelectedFile();

            if (directoryFile != null) {
                Alerts.question(this, String.format("Do you want to import \"%s\" recursively?", directoryFile.toString()),
                        shouldRecur -> TaskDialog.init(this, "Importing local directory...")
                                .thenRun(new ImportLocalDirectoryTask(directoryFile.toPath(), shouldRecur))
                                .done(MainWindow.this::populateDirectoryTree));
            }
        });
    }

    private void addPinterestBoards() {
        final PinterestBoardsSheet pinterestBoardsSheet = BXMLLoader.load(PinterestBoardsSheet.class);

        pinterestBoardsSheet.open(this, sheet -> {
            if (pinterestBoardsSheet.getResult()) {
                final List<PinterestBoard> selectedBoards = pinterestBoardsSheet.getSelectedBoards();
                selectedBoards.forEach(PinterestBoard::save);

                final TaskDialog taskDialog = TaskDialog.init(this, "Importing board sections...");
                selectedBoards.forEach(board -> taskDialog.thenRun(new ImportPinterestBoardSectionsTask(board)));
                taskDialog.done(this::populateDirectoryTree);
            }
        });
    }

    private void indexAllDirectories() {
        final String qMessage = "Should I index everything or just the recently added items?";
        final String[] qOptions = {"Recent", "All", "Cancel"};
        Alerts.multiOptionQuestion(this, qMessage, qOptions, result -> {
            if (!"Cancel".equals(result)) {
                final boolean doReindex = "Recent".equals(result);
                final TaskDialog taskDialog = TaskDialog.init(this, "Indexing all directories...");
                final IndexTask indexTask = IndexTask.inDialog(taskDialog, doReindex);

                taskDialog.thenRun(new LoadRootDirectoriesTask(), directories -> {
                    directories.forEach(indexTask::addIndexTask);
                    taskDialog.done(this::populateDirectoryTree);
                });
            }
        });
    }

    private void openDuplicateScanner() {
        // TODO
    }

    public void populateDirectoryTree() {
        TaskDialog.init(this, "Loading...")
                .thenRun(new LoadRootDirectoriesTask(), this.directoryTreeView::setDirectories)
                .done();
    }

}
