package nl.juraji.imagemanager.fxml.controls;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import nl.juraji.imagemanager.fxml.dialogs.MoveMetaDataDialog;
import nl.juraji.imagemanager.fxml.dialogs.WorkDialog;
import nl.juraji.imagemanager.model.domain.local.MetaData;
import nl.juraji.imagemanager.tasks.DeleteMetaDataTask;
import nl.juraji.imagemanager.util.DesktopUtils;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.fxml.AlertBuilder;
import nl.juraji.imagemanager.util.fxml.ContextMenuBuilder;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.fxml.FXMLStage;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public class MetaDataTile extends VBox implements Initializable {
    private final MetaData metaData;

    @FXML
    private ImageView imageView;

    @FXML
    private Label directoryLabel;

    @FXML
    private Label fileNameLabel;

    @FXML
    private Label fileSizeDimensionsLabel;

    @FXML
    private Button moveItemButton;

    @FXML
    private Button deleteFromDiskButton;

    public MetaDataTile(MetaData metaData) {
        this.metaData = metaData;

        // Load FXML
        final URL url = MetaDataTile.class.getResource("/nl/juraji/imagemanager/fxml/controls/MetaDataTile.fxml");
        final FXMLLoader fxmlLoader = new FXMLLoader(url);

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Setup context menu
        ContextMenuBuilder.build(metaData)
                .appendItem("Open in viewer", this::openInViewerAction)
                .setOnShowing(i -> this.setSelectedStyle())
                .setOnHiding(i -> this.setUnselectedStyle())
                .bindTo(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.updateView();

        // Install tile tooltip
        final Tooltip tileTooltip = new Tooltip(metaData.getPath().toString());
        Tooltip.install(this, tileTooltip);
    }

    private void updateView() {
        final boolean fileExists = FileUtils.exists(metaData.getPath());

        if (fileExists) {
            // Load image
            final Image image = new Image(metaData.getPath().toUri().toString(), true);
            this.imageView.setImage(image);
        } else {
            this.imageView.setImage(null);
        }

        // Set labels
        this.directoryLabel.setText(metaData.getDirectory().getName());

        this.fileNameLabel.setText(metaData.getPath().toString());
        if (!fileExists) {
            this.fileNameLabel.setTextFill(Color.GRAY);
        }

        this.fileSizeDimensionsLabel.setText(String.format("%s %dx%d",
                FileUtils.getHumanReadableSize(metaData.getFileSize()),
                metaData.getWidth(),
                metaData.getHeight()));

        // Setup buttons
        if (fileExists) {
            moveItemButton.setOnAction(e -> this.moveAction(metaData));
            deleteFromDiskButton.setOnAction(e -> this.deleteAction(metaData));
        } else {
            moveItemButton.setOnAction(null);
            deleteFromDiskButton.setOnAction(null);
            moveItemButton.setDisable(true);
            deleteFromDiskButton.setDisable(true);
        }
    }

    private void openInViewerAction(MetaData metaData) {
        DesktopUtils.openFile(metaData.getPath());
    }

    private void moveAction(MetaData metaData) {
        final FXMLStage<MoveMetaDataDialog> fxmlStage = Controller.init(MoveMetaDataDialog.class, "Move item", getWindow());
        final MoveMetaDataDialog controller = fxmlStage.getController();

        controller.setMetaDataToMove(Collections.singletonList(metaData));

        controller.onItemsMoved(movedMetaData -> {
            if (!movedMetaData.isEmpty()) {
                this.updateView();
            }
        });

        controller.setOnClose(this::setUnselectedStyle);

        this.setSelectedStyle();
        fxmlStage.show();
    }

    private void deleteAction(MetaData metaData) {
        this.setSelectedStyle();
        final boolean doDelete = AlertBuilder.confirm(getWindow())
                .withTitle("Delete item")
                .withMessage("Are you sure you want to delete this item?\n" +
                        "The item will be deleted from Image Manager AND the local disk!")
                .showAndWait();

        if (doDelete) {
            final WorkDialog<MetaData> wd = new WorkDialog<>(getWindow());
            wd.exec(new DeleteMetaDataTask(metaData, true));
            wd.addTaskEndNotification(item -> {
                if (item != null) {
                    this.updateView();
                } else {
                    AlertBuilder.warning(getWindow())
                            .withTitle("Deletion failed")
                            .withMessage("Could not delete item, please delete it manually.")
                            .showAndWait();
                }

                this.setUnselectedStyle();
            });
        } else {
            this.setUnselectedStyle();
        }
    }

    private Window getWindow() {
        return this.getScene().getWindow();
    }

    private void setSelectedStyle() {
        this.setStyle("-fx-border-color: gray; -fx-border-width: 1px;");
    }

    private void setUnselectedStyle() {
        this.setStyle("-fx-border-color: transparent; -fx-border-width: 1px;");
    }
}
