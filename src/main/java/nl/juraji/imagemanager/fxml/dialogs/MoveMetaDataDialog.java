package nl.juraji.imagemanager.fxml.dialogs;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import nl.juraji.imagemanager.model.domain.local.Directory;
import nl.juraji.imagemanager.model.domain.local.MetaData;
import nl.juraji.imagemanager.model.finders.DirectoryFinder;
import nl.juraji.imagemanager.tasks.MoveMetaDataTask;
import nl.juraji.imagemanager.util.fxml.AlertBuilder;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTaskChain;
import nl.juraji.imagemanager.util.types.ListAdditionListener;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 8-12-2018.
 * Image Manager 2
 */
public class MoveMetaDataDialog extends Controller implements Initializable {
    private final SimpleListProperty<MetaData> metaDataToMove = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final SimpleListProperty<MetaData> itemsMoved = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Button moveItemButton;
    public ChoiceBox<DirectoryChoiceBoxItem> directoryChoiceBox;

    public void setMetaDataToMove(List<MetaData> metaData) {
        this.metaDataToMove.addAll(metaData);
    }

    public void onItemsMoved(Consumer<List<? extends MetaData>> onItemMoved) {
        this.itemsMoved.addListener((ListAdditionListener<MetaData>) onItemMoved::accept);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moveItemButton.disableProperty().bind(this.directoryChoiceBox.getSelectionModel().selectedItemProperty().isNull());

        metaDataToMove.addListener((ListAdditionListener<MetaData>) newValue -> {
            this.directoryChoiceBox.getItems().clear();

            if (newValue.size() > 0) {
                List<DirectoryChoiceBoxItem> items = DirectoryFinder.find().all().stream()
                        .sorted(Comparator.comparing(Directory::getLocationOnDisk))
                        .map(DirectoryChoiceBoxItem::new)
                        .collect(Collectors.toList());

                this.directoryChoiceBox.getItems().addAll(items);
            }
        });
    }

    public void moveItem() {
        final DirectoryChoiceBoxItem selectedItem = this.directoryChoiceBox.getValue();
        if (selectedItem == null) {
            return;
        }

        final Directory directory = selectedItem.directory;
        final List<MetaData> metaData = metaDataToMove.stream().filter(Objects::nonNull).collect(Collectors.toList());
        final List<MetaData> movedItems = new ArrayList<>();

        final ManagerTaskChain<MetaData, MetaData> taskChain = new ManagerTaskChain<>(metaData, true);

        taskChain.nextTask(m -> new MoveMetaDataTask(m, directory));
        taskChain.afterEach(movedItems::add);

        taskChain.afterAll(() -> {
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

        new WorkDialog<Void>(getStage()).exec(taskChain);
    }

    private static class DirectoryChoiceBoxItem {
        private final Directory directory;

        private DirectoryChoiceBoxItem(Directory directory) {
            this.directory = directory;
        }

        @Override
        public String toString() {
            final String s = directory.getLocationOnDisk().toString();
            final String name = s.length() > 40 ? s.substring(s.length() - 40) : s;
            return String.format("...%s", name);
        }
    }
}
