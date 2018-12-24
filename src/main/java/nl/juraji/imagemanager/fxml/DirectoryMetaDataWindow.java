package nl.juraji.imagemanager.fxml;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import nl.juraji.imagemanager.fxml.controls.MetaDataLabel;
import nl.juraji.imagemanager.fxml.dialogs.MoveMetaDataDialog;
import nl.juraji.imagemanager.fxml.dialogs.WorkDialog;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.BaseModel;
import nl.juraji.imagemanager.model.domain.Settings;
import nl.juraji.imagemanager.model.domain.pinterest.PinMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.model.finders.SettingsFinder;
import nl.juraji.imagemanager.tasks.DeleteMetaDataTask;
import nl.juraji.imagemanager.tasks.pinterest.DeletePinTask;
import nl.juraji.imagemanager.util.DesktopUtils;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.StringUtils;
import nl.juraji.imagemanager.util.fxml.AlertBuilder;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.fxml.FXMLStage;
import nl.juraji.imagemanager.util.fxml.OptionDialogBuilder;
import nl.juraji.imagemanager.util.types.NullSafeBinding;
import nl.juraji.imagemanager.util.types.ValueListener;

import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 2-12-2018.
 * Image Manager 2
 */
public class DirectoryMetaDataWindow extends Controller implements Initializable {

    private final Image checkImage = new Image("/nl/juraji/imagemanager/images/check.png", true);
    private final SimpleObjectProperty<BaseDirectory> directory = new SimpleObjectProperty<>();
    private final SimpleListProperty<MetaDataLabel> metaDataLabels = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final FilteredList<MetaDataLabel> filteredMetaDataLabels = new FilteredList<>(metaDataLabels, s -> true);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
            .withZone(ZoneId.systemDefault());
    private NullSafeBinding<BaseMetaData> selectedMetaData;

    public VBox actionButtonContainer;
    public Label directoryNameLabel;
    public Label imageCountLabel;
    public Label originLabel;
    public TextField searchTextField;
    public ListView<MetaDataLabel> metaDataListView;
    public BorderPane imageViewContainer;
    public ImageView imageView;
    public Label localPathLabel;
    public Label dimensionsLabel;
    public Label downloadedOnLabel;
    public TextArea commentsTextArea;
    public ImageView hashCreatedImageView;
    public MenuButton metaDataOptionsMenu;

    public void setDirectory(BaseDirectory directory) {
        this.directory.set(directory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        commentsTextArea.setWrapText(true);

        final MultipleSelectionModel<MetaDataLabel> selectionModel = metaDataListView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        selectionModel.clearSelection();
        imageView.fitWidthProperty().bind(imageViewContainer.widthProperty());
        imageView.fitHeightProperty().bind(imageViewContainer.heightProperty());
        selectedMetaData = NullSafeBinding.create(() ->
                        selectionModel.selectedItemProperty().get().getMetaData(),
                selectionModel.selectedItemProperty());

        // Bind directory name
        final NullSafeBinding<String> directoryNameBinding = NullSafeBinding.create(() ->
                directory.get().getName(), directory);
        this.directoryNameLabel.textProperty().bind(directoryNameBinding);

        // Bind image count
        final NullSafeBinding<String> imageCountBinding = NullSafeBinding.create(() ->
                directory.get().getMetaDataCount() + " images", directory);
        this.imageCountLabel.textProperty().bind(imageCountBinding);


        // Bind directory source
        final NullSafeBinding<String> originBinding = NullSafeBinding.create(() ->
                "Source: " + directory.get().getOrigin(), directory);
        this.originLabel.textProperty().bind(originBinding);

        // Bind metadata list to directory metadata
        directory.addListener((ValueListener<BaseDirectory>) newValue -> {
            metaDataLabels.clear();
            ((Set<BaseMetaData>) newValue.getMetaData()).stream()
                    .sorted(Comparator.comparing(BaseModel::getModified).reversed())
                    .map(MetaDataLabel::new)
                    .forEach(metaDataLabels::add);
        });

        searchTextField.textProperty().addListener((ValueListener<String>) newValue -> {
            this.metaDataListView.getSelectionModel().clearSelection();
            if (StringUtils.isEmpty(newValue)) {
                this.filteredMetaDataLabels.setPredicate(s -> true);
            } else {
                this.filteredMetaDataLabels.setPredicate(s ->
                        s.getMetaData().getPath().getFileName().toString().contains(newValue));
            }
        });

        this.metaDataListView.setItems(filteredMetaDataLabels);

        // Bind metadata list selection to update view logic
        final NullSafeBinding<Image> imageBinding = NullSafeBinding.create(() ->
                new Image(selectedMetaData.get().getPath().toUri().toString(), true), selectedMetaData);
        imageView.imageProperty().bind(imageBinding);

        final NullSafeBinding<String> localPathBinding = NullSafeBinding.create(() ->
                selectedMetaData.get().getPath().toString(), selectedMetaData);
        localPathLabel.textProperty().bind(localPathBinding);

        final NullSafeBinding<String> dimensionsBinding = NullSafeBinding.create(() -> {
            final BaseMetaData metaData = selectedMetaData.get();
            return String.valueOf(metaData.getWidth()) + 'x' + metaData.getHeight() +
                    ' ' + FileUtils.getHumanReadableSize(metaData.getFileSize());
        }, selectedMetaData);
        dimensionsLabel.textProperty().bind(dimensionsBinding);

        final NullSafeBinding<String> downloadedOnBinding = NullSafeBinding.create(() ->
                dateTimeFormatter.format(selectedMetaData.get().getCreated()), selectedMetaData);
        downloadedOnLabel.textProperty().bind(downloadedOnBinding);

        selectedMetaData.addListener((ValueListener<BaseMetaData>) metaData -> {
            if (metaData == null) {
                commentsTextArea.setText(null);
            } else {
                commentsTextArea.setText(metaData.getComments());
            }
        });
        commentsTextArea.disableProperty().bind(selectedMetaData.isNull());

        final NullSafeBinding<Image> hashPresentBinding = NullSafeBinding.create(() ->
                selectedMetaData.get().getHash() == null ? null : checkImage, selectedMetaData);
        hashCreatedImageView.imageProperty().bind(hashPresentBinding);

        actionButtonContainer.getChildren().forEach(node ->
                node.disableProperty().bind(selectedMetaData.isNull()));

        NullSafeBinding.create(directory::get, directory).addListener((ValueListener<BaseDirectory>) d -> {
            // If directory is a PinterestBoard
            // Add actions to options menu related to pins
            final ObservableList<MenuItem> optionsMenuItems = metaDataOptionsMenu.getItems();
            optionsMenuItems.clear();

            final MenuItem openInViewer = new MenuItem("Open in viewer");
            openInViewer.setOnAction(e -> this.openInViewerAction());
            optionsMenuItems.add(openInViewer);

            if (d instanceof PinterestBoard) {
                final MenuItem openPinInPinterest = new MenuItem("Open pin in Pinterest");
                openPinInPinterest.setOnAction(e -> this.openPinInPinterestAction());
                optionsMenuItems.add(openPinInPinterest);
                final MenuItem openBoardInPinterest = new MenuItem("Open board in Pinterest");
                openBoardInPinterest.setOnAction(e -> this.openBoardInPinterestAction());
                optionsMenuItems.add(openBoardInPinterest);
            }
        });
    }

    private void openInViewerAction() {
        final MultipleSelectionModel<MetaDataLabel> selectionModel = metaDataListView.getSelectionModel();
        final MetaDataLabel selectedItem = selectionModel.getSelectedItem();

        if (selectedItem != null) {
            DesktopUtils.openFile(selectedItem.getMetaData().getPath());
        }
    }

    public void deleteAction() {
        final MultipleSelectionModel<MetaDataLabel> selectionModel = metaDataListView.getSelectionModel();
        final MetaDataLabel selectedItem = selectionModel.getSelectedItem();

        if (selectedItem != null) {
            final BaseMetaData metaData = selectedItem.getMetaData();
            final int option = OptionDialogBuilder.build(getStage())
                    .withTitle("Delete " + selectedItem.getText())
                    .withMessage("Should I delete the index only or the image from disk as well?\n" +
                            "This action can not be undone!")
                    .withOption("Index only")
                    .withOption("Index and from disk")
                    .show();

            if (option > -1) {
                final WorkDialog<BaseMetaData> wd = new WorkDialog<>(getStage());

                if (option == 1) {
                    if (metaData instanceof PinMetaData) {
                        final boolean doDeletePin = AlertBuilder.confirm(getStage())
                                .withTitle("Delete from Pinterest")
                                .withMessage("Do you want this pin to be deleted from Pinterest as well?")
                                .showAndWait();
                        if (doDeletePin) {
                            wd.exec(new DeletePinTask((PinMetaData) metaData));
                        }
                    } else {
                        wd.exec(new DeleteMetaDataTask(metaData, true));
                    }
                } else {
                    wd.exec(new DeleteMetaDataTask(metaData, false));
                }

                wd.addTaskEndNotification(deletedItem -> {
                    if (deletedItem != null) {
                        metaDataLabels.remove(selectedItem);
                        selectionModel.clearSelection();
                    } else {
                        AlertBuilder.warning(getStage())
                                .withTitle("Deletion failed")
                                .withMessage("Could not delete item, please delete it manually.")
                                .show();
                    }
                });
            }
        }
    }

    public void openDirectoryAction() {
        DesktopUtils.openFile(directory.get().getLocationOnDisk());
    }

    public void saveAction() {
        final BaseMetaData metaData = selectedMetaData.get();
        if (metaData != null) {
            metaData.setComments(commentsTextArea.getText());
            metaData.save();
        }
    }

    public void moveAction() {
        final ObservableList<MetaDataLabel> selectedItems = metaDataListView.getSelectionModel().getSelectedItems();
        if (selectedItems.size() > 0) {
            final FXMLStage<MoveMetaDataDialog> fxmlStage = Controller.init(MoveMetaDataDialog.class, "Move item", getStage());
            final MoveMetaDataDialog controller = fxmlStage.getController();

            controller.setMetaDataToMove(selectedItems.stream()
                    .map(MetaDataLabel::getMetaData)
                    .collect(Collectors.toList()));

            controller.onItemsMoved(movedMetaData -> {
                metaDataListView.getSelectionModel().clearSelection();
                movedMetaData.forEach(m -> metaDataLabels.removeIf(l -> l.getMetaData().getId().equals(m.getId())));
            });

            fxmlStage.show();
        }
    }

    public void openPinInPinterestAction() {
        DesktopUtils.openWebUri(((PinMetaData) selectedMetaData.get()).getPinterestUri());
    }

    public void openBoardInPinterestAction() {
        DesktopUtils.openWebUri(((PinterestBoard) directory.get()).getBoardUrl());
    }
}
