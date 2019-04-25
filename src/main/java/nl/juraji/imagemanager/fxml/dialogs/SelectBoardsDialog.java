package nl.juraji.imagemanager.fxml.dialogs;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.util.fxml.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Juraji on 8-12-2018.
 * Image Manager 2
 */
public class SelectBoardsDialog extends Controller implements Initializable {
    private final SimpleListProperty<PinterestBoard> availableItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final SimpleListProperty<PinterestBoard> selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());

    public TableView<PinterestBoard> itemsTableView;

    public void addAvailableItems(List<PinterestBoard> availableItems) {
        this.availableItems.addAll(availableItems);
    }

    public void onItemsSelected(ListChangeListener<PinterestBoard> listener) {
        selectedItems.get().addListener(listener);
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.itemsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.itemsTableView.itemsProperty().bind(availableItems);
    }

    public void processSelectedItems() {
        final ObservableList<PinterestBoard> items = this.itemsTableView.getSelectionModel().getSelectedItems();

        if (!items.isEmpty()) {
            selectedItems.addAll(items);
        }

        this.close();
    }
}
