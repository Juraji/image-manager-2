package nl.juraji.imagemanager.pivot.controls;

import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.pivot.frames.DirectoryFrame;
import nl.juraji.imagemanager.pivot.MainWindow;
import nl.juraji.imagemanager.pivot.dialogs.TaskDialog;
import nl.juraji.imagemanager.tasks.IndexTask;
import nl.juraji.imagemanager.util.DesktopUtils;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.pivot.Alerts;
import nl.juraji.imagemanager.util.pivot.BXMLLoader;
import nl.juraji.imagemanager.util.pivot.menus.ContextMenuHandler;
import nl.juraji.imagemanager.util.pivot.menus.builder.MenuBuilder;
import org.apache.pivot.collections.Sequence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by Juraji on 16-5-2019.
 * image-manager
 */
public class DirectoryTreeContextMenu extends ContextMenuHandler {

    private final MainWindow owner;
    private final DirectoryTreeView directoryTreeView;

    public DirectoryTreeContextMenu(MainWindow owner, DirectoryTreeView directoryTreeView) {
        this.owner = owner;
        this.directoryTreeView = directoryTreeView;
    }

    @Override
    protected void buildMenu(MenuBuilder builder, int x, int y) {
        final DirectoryTreeView.DirectoryNode directoryNode = directoryTreeView.selectNodeAt(y);
        final boolean isPinterestBoard = directoryNode.getDirectory() instanceof PinterestBoard;

        builder.section()
                .item("View directory", () -> this.viewDirectoryAction(directoryNode))
                .section()
                .item("Index this directory", () -> this.indexDirectoryAction(directoryNode))
                .item("Scan for duplicates", () -> this.scanForDuplicates(directoryNode))
                .section()
                .item("Open in file browser", () -> this.openInFileBrowser(directoryNode))
                .item("Open in Pinterest", () -> this.openInPinterest(directoryNode), isPinterestBoard)
                .section()
                .item("Delete", () -> this.deleteDirectory(directoryNode));
    }

    private void viewDirectoryAction(DirectoryTreeView.DirectoryNode directoryNode) {
        final DirectoryFrame directoryWindow = BXMLLoader.load(DirectoryFrame.class, directoryNode.getDirectory());
        directoryWindow.setLocation(owner.getLocation());
        directoryWindow.open(owner);
    }

    private void indexDirectoryAction(DirectoryTreeView.DirectoryNode directoryNode) {
        final String qMessage = "Should I index everything or just the recently added items?";
        final String[] qOptions = {"Recent", "All", "Cancel"};
        Alerts.multiOptionQuestion(owner, qMessage, qOptions, result -> {
            if (!"Cancel".equals(result)) {
                final boolean doReindex = "Recent".equals(result);
                final BaseDirectory directory = directoryNode.getDirectory();
                final TaskDialog taskDialog = TaskDialog.init(owner, "Indexing \"" + directory.getName() + "\"...");
                final IndexTask indexTask = IndexTask.inDialog(taskDialog, doReindex);

                indexTask.addIndexTask(directory);
                taskDialog.done(owner::populateDirectoryTree);
            }
        });
    }

    private void scanForDuplicates(DirectoryTreeView.DirectoryNode directoryNode) {

    }

    private void openInFileBrowser(DirectoryTreeView.DirectoryNode directoryNode) {
        DesktopUtils.openFile(directoryNode.getDirectory().getLocationOnDisk());
    }

    private void openInPinterest(DirectoryTreeView.DirectoryNode directoryNode) {
        if (directoryNode.getDirectory() instanceof PinterestBoard) {
            final PinterestBoard board = (PinterestBoard) directoryNode.getDirectory();
            DesktopUtils.openWebUri(board.getBoardUrl());
        }
    }

    private void deleteDirectory(DirectoryTreeView.DirectoryNode directoryNode) {
        final String[] options = {"Index only", "Index and files", "No"};

        final Consumer<String> action = option -> {
            if (options[0].equals(option)) {
                deleteDirectoryIndex(directoryNode);
            } else if (options[1].equals(option)) {
                try {
                    this.deleteDirectoryFiles(directoryNode);
                    this.deleteDirectoryIndex(directoryNode);
                } catch (IOException e) {
                    Alerts.error(owner, String.format("Could not delete directory %s, check if it exists and " +
                            "you have permission to delete it.", directoryNode.getDirectory().getName()));
                }
            }
        };

        Alerts.multiOptionQuestion(owner, String.format("Are you sure you want to delete \"%s\"?",
                directoryNode.getDirectory().getName()), options, action);
    }

    private void deleteDirectoryIndex(DirectoryTreeView.DirectoryNode directoryNode) {
        final BaseDirectory directory = directoryNode.getDirectory();
        final Sequence.Tree.Path path = directoryTreeView.getSelectedPath();

        directory.delete();
        this.directoryTreeView.removeNodeAt(path);
    }

    private void deleteDirectoryFiles(DirectoryTreeView.DirectoryNode directoryNode) throws IOException {
        final Path locationOnDisk = directoryNode.getDirectory().getLocationOnDisk();

        if (FileUtils.exists(locationOnDisk)) {
            FileUtils.delete(locationOnDisk);
        }
    }
}
