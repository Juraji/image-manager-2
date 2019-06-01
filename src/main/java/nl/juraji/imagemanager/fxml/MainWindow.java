package nl.juraji.imagemanager.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import nl.juraji.imagemanager.Main;
import nl.juraji.imagemanager.fxml.controls.DirectoryTreeTableView;
import nl.juraji.imagemanager.fxml.dialogs.AddLocalDirectoryDialog;
import nl.juraji.imagemanager.fxml.dialogs.WorkDialog;
import nl.juraji.imagemanager.model.domain.local.Directory;
import nl.juraji.imagemanager.tasks.*;
import nl.juraji.imagemanager.util.DesktopUtils;
import nl.juraji.imagemanager.util.fxml.AlertBuilder;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.fxml.FXMLStage;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTaskChain;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 23-11-2018.
 * Image Manager 2
 */
public class MainWindow extends Controller implements Initializable {

    public DirectoryTreeTableView directoriesTableView;
    public Label totalImagesStatusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        directoriesTableView.loadDirectoryTrees();
        totalImagesStatusLabel.textProperty().bind(directoriesTableView.totalImagesProperty().asString());
    }

    public void menuSystemSettingsAction() {
        final FXMLStage<SettingsWindow> settingsStage = Controller.init(SettingsWindow.class,
                "Image Manager Settings", getStage());
        settingsStage.show();
    }

    public void menuSystemViewLogsAction() {
        DesktopUtils.openFile(Paths.get("./logs"));
    }

    public void menuSystemExitAction() {
        Main.exit();
    }

    public void menuDirectoriesAddLocalDirectoryAction() {
        final AddLocalDirectoryDialog dialog = new AddLocalDirectoryDialog(getStage());

        final AddLocalDirectoryDialog.AddDirectoryResult result = dialog.showAndWait().orElse(null);
        if (result != null && !result.isCanceled()) {
            final File path = result.getPath();
            final boolean recursive = result.isRecursive();

            if (path.isDirectory()) {
                final WorkDialog<Directory> wd = new WorkDialog<>(getStage());
                wd.addTaskEndNotification(localDirectory -> {
                    if (localDirectory == null) {
                        directoriesTableView.loadDirectoryTrees();
                    } else {
                        directoriesTableView.addDirectory(localDirectory);
                    }
                });
                wd.exec(new ImportLocalDirectoryTask(path.toPath(), recursive));
            } else {
                AlertBuilder.warning(getStage())
                        .withTitle("Invalid directory")
                        .withTitle("You did not specify a valid or existing directory.")
                        .show();
            }
        }
    }

    public void menuDirectoriesIndexAllDirectoriesAction(ActionEvent event) {
        // Get from loaded data, since it's always up to date
        final List<Directory> directories = directoriesTableView.getRoot().getChildren().stream()
                .map(TreeItem::getValue)
                .collect(Collectors.toList());

        final Window window = ((MenuItem) event.getTarget()).getParentPopup().getOwnerWindow();
        final WorkDialog<Void> dialog = new WorkDialog<>(window);
        dialog.exec(new DefaultDirectoriesTask(directories));
    }

    public void menuDirectoriesIndexFavoriteDirectoriesAction(ActionEvent event) {
        // Get from loaded data, since it's always up to date
        final List<Directory> directories = directoriesTableView.getRoot().getChildren().stream()
                .map(TreeItem::getValue)
                .filter(Directory::isFavorite)
                .collect(Collectors.toList());

        final Window window = ((MenuItem) event.getTarget()).getParentPopup().getOwnerWindow();
        final WorkDialog<Void> dialog = new WorkDialog<>(window);
        dialog.addTaskEndNotification(o -> directoriesTableView.refresh());
        dialog.exec(new DefaultDirectoriesTask(directories));
    }

    public void menuToolsDuplicateScannerAction() {
        final FXMLStage<DuplicateScannerWindow> fxmlStage = Controller.init(DuplicateScannerWindow.class,
                "Image Manager - Duplicate Scanner", getStage());

        fxmlStage.onClose(directoriesTableView::loadDirectoryTrees);
        fxmlStage.show();
    }

    public void menuToolsGenerateMissingHashesAction() {
        // Get from loaded data, since it's always up to date
        final List<Directory> directories = directoriesTableView.getRoot().getChildren().stream()
                .map(TreeItem::getValue)
                .collect(Collectors.toList());

        final WorkDialog<Void> dialog = new WorkDialog<>(getStage());
        final ManagerTaskChain<Directory, Void> task = new ManagerTaskChain<Directory, Void>(directories)
                .nextTask(HashDirectoryTask::new);
        dialog.exec(task);
    }

    public void menuToolsAllDeleteHashesAction() {
        final boolean doDelete = AlertBuilder.warning(getStage())
                .withTitle("Delete all hashes")
                .withMessage("Are you sure you want to delete all generated hashes?\n" +
                        "You can regenerate hashes at any time by indexing the directories.")
                .showAndWait();

        if (doDelete) {
            // Get from loaded data, since it's always up to date
            final List<Directory> directories = directoriesTableView.getRoot().getChildren().stream()
                    .map(TreeItem::getValue)
                    .collect(Collectors.toList());

            final ManagerTaskChain<Directory, Void> taskChain = new ManagerTaskChain<Directory, Void>(directories)
                    .nextTask(DeleteHashesTask::new);


            new WorkDialog<Void>(getStage()).exec(taskChain);
        }
    }

    public void directoriesTableViewMouseClickedAction(MouseEvent event) {
        if (MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() == 2) {
            final TreeItem<Directory> selectedItem = directoriesTableView.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                final Directory directory = selectedItem.getValue();
                final FXMLStage<DirectoryMetaDataWindow> fxmlStage = Controller.init(DirectoryMetaDataWindow.class,
                        "Directory - " + directory.getName(), getStage());

                final DirectoryMetaDataWindow controller = fxmlStage.getController();
                controller.setDirectory(directory);

                fxmlStage.show();
                fxmlStage.onClose(directoriesTableView::loadDirectoryTrees);
            }
        }
    }
}
