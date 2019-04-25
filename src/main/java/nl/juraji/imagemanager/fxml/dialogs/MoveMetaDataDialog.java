package nl.juraji.imagemanager.fxml.dialogs;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.BaseMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinMetaData;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.model.finders.LocalDirectoriesFinder;
import nl.juraji.imagemanager.model.finders.PinterestBoardsFinder;
import nl.juraji.imagemanager.tasks.MoveMetaDataTask;
import nl.juraji.imagemanager.tasks.pinterest.MovePinTask;
import nl.juraji.imagemanager.util.fxml.AlertBuilder;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.types.ListAdditionListener;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 8-12-2018.
 * Image Manager 2
 */
public class MoveMetaDataDialog extends Controller implements Initializable {
    private final SimpleListProperty<BaseMetaData> metaDataToMove = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final SimpleListProperty<BaseMetaData> itemsMoved = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Button moveItemButton;
    public ChoiceBox<DirectoryChoiceBoxItem> directoryChoiceBox;

    public void setMetaDataToMove(List<BaseMetaData> metaData) {
        this.metaDataToMove.addAll(metaData);
    }

    public void onItemsMoved(Consumer<List<? extends BaseMetaData>> onItemMoved) {
        this.itemsMoved.addListener((ListAdditionListener<BaseMetaData>) onItemMoved::accept);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moveItemButton.disableProperty().bind(this.directoryChoiceBox.getSelectionModel().selectedItemProperty().isNull());

        metaDataToMove.addListener((ListAdditionListener<BaseMetaData>) newValue -> {
            this.directoryChoiceBox.getItems().clear();

            if (newValue.size() > 0) {
                final boolean isPins = this.checkIsPins(newValue);

                List<DirectoryChoiceBoxItem> items;
                if (isPins) {
                    items = PinterestBoardsFinder.find().all().stream()
                            .sorted(Comparator.comparing(BaseDirectory::getLocationOnDisk))
                            .map(DirectoryChoiceBoxItem::new)
                            .collect(Collectors.toList());
                } else {
                    items = LocalDirectoriesFinder.find().all().stream()
                            .sorted(Comparator.comparing(BaseDirectory::getLocationOnDisk))
                            .map(DirectoryChoiceBoxItem::new)
                            .collect(Collectors.toList());
                }

                this.directoryChoiceBox.getItems().addAll(items);
            }
        });
    }

    public void moveItem() {
        final DirectoryChoiceBoxItem selectedItem = this.directoryChoiceBox.getValue();
        if (selectedItem == null) {
            return;
        }

        final BaseDirectory directory = selectedItem.directory;
        final AtomicBoolean doMoveOnPinterest = new AtomicBoolean(false);

        if (this.checkIsPins(metaDataToMove)) {
            final boolean movePinterestPins = AlertBuilder.confirm(getStage())
                    .withTitle("Move Pinterest pins")
                    .withMessage("Do you want the selected pin(s) to be moved to %s on Pinterest as well?",
                            directory.getName())
                    .showAndWait();

            doMoveOnPinterest.set(movePinterestPins);
        }

        final WorkQueueDialog<BaseMetaData> queueDialog = new WorkQueueDialog<>(getStage());

        metaDataToMove.stream()
                .filter(Objects::nonNull)
                .forEach(metaData -> {
                    if (doMoveOnPinterest.get()) {
                        queueDialog.queue(new MovePinTask((PinMetaData) metaData, (PinterestBoard) directory));
                    }

                    queueDialog.queue(new MoveMetaDataTask(metaData, directory));
                });

        queueDialog.addQueueEndNotification(movedItems -> {
            if (!movedItems.isEmpty()) {
                AlertBuilder.info(getStage())
                        .withTitle("Items moved")
                        .withMessage("Moved %d items to %s", movedItems.size(), directory.getName())
                        .showAndWait();
            }

            if (movedItems.size() != metaDataToMove.size()) {
                AlertBuilder.info(getStage())
                        .withTitle("Some moves failed")
                        .withMessage("%d items failed to move, move them manually or try again later.",
                                metaDataToMove.size() - movedItems.size())
                        .showAndWait();
            }

            itemsMoved.addAll(movedItems);
            close();
        });

        queueDialog.execute();
    }

    private boolean checkIsPins(List<? extends BaseMetaData> metaDataToMove) {
        return metaDataToMove.stream().anyMatch(m -> m instanceof PinMetaData);
    }

    private static class DirectoryChoiceBoxItem {
        private final BaseDirectory directory;

        private DirectoryChoiceBoxItem(BaseDirectory directory) {
            this.directory = directory;
        }

        @Override
        public String toString() {
            final String s = directory.getLocationOnDisk().toString();
            final String name = s.length() > 40 ? s.substring(s.length() - 40) : s;
            return String.format("(%s) ...%s", directory.getOrigin(), name);
        }
    }
}
