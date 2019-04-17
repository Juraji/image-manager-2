package nl.juraji.imagemanager.fxml;

import io.ebean.Model;
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
import nl.juraji.imagemanager.fxml.dialogs.SelectBoardsDialog;
import nl.juraji.imagemanager.fxml.dialogs.WorkDialog;
import nl.juraji.imagemanager.fxml.dialogs.WorkQueueDialog;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.local.LocalDirectory;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.model.domain.settings.Settings;
import nl.juraji.imagemanager.model.finders.SettingsFinder;
import nl.juraji.imagemanager.model.finders.WebCookieFinder;
import nl.juraji.imagemanager.tasks.DeleteHashesTask;
import nl.juraji.imagemanager.tasks.ImportLocalDirectoryTask;
import nl.juraji.imagemanager.tasks.IndexDirectoryTaskBuilder;
import nl.juraji.imagemanager.tasks.pinterest.FetchPinterestBoardsTask;
import nl.juraji.imagemanager.tasks.pinterest.ImportPinterestBoardSectionsTask;
import nl.juraji.imagemanager.util.DesktopUtils;
import nl.juraji.imagemanager.util.fxml.AlertBuilder;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.fxml.FXMLStage;
import nl.juraji.imagemanager.util.types.ListAdditionListener;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static nl.juraji.imagemanager.tasks.pinterest.PinterestWebTask.PINTEREST_BASE_URI;

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
                final WorkDialog<LocalDirectory> wd = new WorkDialog<>(getStage());
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

    public void menuDirectoriesAddPinterestBoardAction(ActionEvent event) {
        final boolean isAuth = WebCookieFinder.find().cookieValueEquals(PINTEREST_BASE_URI.getHost(), "_auth", "1");
        if (isAuth) {
            final WorkDialog<List<PinterestBoard>> wd = new WorkDialog<>(getStage());

            wd.addTaskEndNotification(boards -> {
                final FXMLStage<SelectBoardsDialog> stage =
                        Controller.init(SelectBoardsDialog.class,
                                "Select Pinterest boards to add", getStage());

                SelectBoardsDialog controller = stage.getController();
                controller.addAvailableItems(boards);

                controller.onItemsSelected((ListAdditionListener<PinterestBoard>) addedItems -> {
                    final WorkQueueDialog<Void> indexSectionsDialog = new WorkQueueDialog<>(getStage());
                    addedItems.stream()
                            .peek(Model::save) // Save boards
                            .peek(directoriesTableView::addDirectory) // Add to table view
                            .map(ImportPinterestBoardSectionsTask::new) // index sections
                            .forEach(indexSectionsDialog::queue);

                    indexSectionsDialog.addQueueEndNotification(directoriesTableView::loadDirectoryTrees);
                    indexSectionsDialog.execute();
                });

                stage.show();
            });

            wd.exec(new FetchPinterestBoardsTask());
        } else {
            final Window window = ((MenuItem) event.getTarget()).getParentPopup().getOwnerWindow();
            AlertBuilder.warning(window)
                    .withTitle("Pinterest integration unavailable")
                    .withMessage("You have not setup Pinterest yet.\n" +
                            "Go to System -> Settings and use the Pinterest login button to log in.")
                    .show();
        }
    }

    public void menuDirectoriesIndexAllDirectoriesAction(ActionEvent event) {
        // Get from loaded data, since it's always up to date
        final List<BaseDirectory> directories = directoriesTableView.getRoot().getChildren().stream()
                .map(TreeItem::getValue)
                .collect(Collectors.toList());

        final Window window = ((MenuItem) event.getTarget()).getParentPopup().getOwnerWindow();
        IndexDirectoryTaskBuilder.standard(window)
                .afterEach(directoriesTableView::refresh)
                .execute(directories);
    }

    public void menuDirectoriesIndexFavoriteDirectoriesAction(ActionEvent event) {
        // Get from loaded data, since it's always up to date
        final List<BaseDirectory> directories = directoriesTableView.getRoot().getChildren().stream()
                .map(TreeItem::getValue)
                .filter(BaseDirectory::isFavorite)
                .collect(Collectors.toList());

        final Window window = ((MenuItem) event.getTarget()).getParentPopup().getOwnerWindow();
        IndexDirectoryTaskBuilder.standard(window)
                .afterEach(directoriesTableView::refresh)
                .execute(directories);
    }

    public void menuDirectoriesOpenTargetDirectoryAction() {
        final Settings settings = SettingsFinder.getSettings();
        final Path defaultTargetDirectory = settings.getDefaultTargetDirectory();
        DesktopUtils.openFile(defaultTargetDirectory);
    }

    public void menuToolsDuplicateScannerAction() {
        final FXMLStage<DuplicateScannerWindow> fxmlStage = Controller.init(DuplicateScannerWindow.class,
                "Image Manager - Duplicate Scanner", getStage());

        fxmlStage.onClose(directoriesTableView::loadDirectoryTrees);
        fxmlStage.show();
    }

    public void menuToolsGenerateMissingHashesAction() {
        // Get from loaded data, since it's always up to date
        final List<BaseDirectory> directories = directoriesTableView.getRoot().getChildren().stream()
                .map(TreeItem::getValue)
                .collect(Collectors.toList());

        IndexDirectoryTaskBuilder.build(getStage())
                .withGenerateHashes()
                .afterAll(directoriesTableView::refresh)
                .execute(directories);
    }

    public void menuToolsAllDeleteHashesAction(ActionEvent event) {
        final boolean doDelete = AlertBuilder.warning(getStage())
                .withTitle("Delete all hashes")
                .withMessage("Are you sure you want to delete all generated hashes?\n" +
                        "You can regenerate hashes at any time by indexing the directories.")
                .showAndWait();

        if (doDelete) {
            // Get from loaded data, since it's always up to date
            final List<BaseDirectory> directories = directoriesTableView.getRoot().getChildren().stream()
                    .map(TreeItem::getValue)
                    .collect(Collectors.toList());

            final Window window = ((MenuItem) event.getTarget()).getParentPopup().getOwnerWindow();
            final WorkQueueDialog<Void> wd = new WorkQueueDialog<>(window);

            directories.forEach(d -> wd.queue(new DeleteHashesTask(d)));

            wd.execute();
        }
    }

    public void directoriesTableViewMouseClickedAction(MouseEvent event) {
        if (MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() == 2) {
            final TreeItem<BaseDirectory> selectedItem = directoriesTableView.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                final BaseDirectory directory = selectedItem.getValue();
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
