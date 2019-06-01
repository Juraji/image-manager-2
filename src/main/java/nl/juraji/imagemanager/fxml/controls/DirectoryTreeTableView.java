package nl.juraji.imagemanager.fxml.controls;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import nl.juraji.imagemanager.model.domain.local.Directory;
import nl.juraji.imagemanager.model.finders.DirectoryFinder;

import java.util.List;

/**
 * Created by Juraji on 25-11-2018.
 * Image Manager 2
 */
public class DirectoryTreeTableView extends TreeTableView<Directory> {

    private final IntegerProperty totalImages = new SimpleIntegerProperty(0);

    public ReadOnlyIntegerProperty totalImagesProperty() {
        return totalImages;
    }

    @SuppressWarnings("unused")
    public DirectoryTreeTableView() {
        this(DirectoryTreeItem.root());
    }

    public DirectoryTreeTableView(TreeItem<Directory> root) {
        super(root);

        this.setShowRoot(false);

        final TreeTableViewSelectionModel<Directory> selectionModel = this.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        this.setContextMenu(new DirectoryTreeTableContextMenu(this));
    }

    public void loadDirectoryTrees() {
        final TreeItem<Directory> root = getRoot();
        final ObservableList<TreeItem<Directory>> rootChildren = root.getChildren();
        rootChildren.clear();

        final List<Directory> rootDirectories = DirectoryFinder.find().rootDirectories();
        rootDirectories.stream()
                .map(DirectoryTreeItem::new)
                .forEach(rootChildren::add);

        final int sum = rootDirectories.stream()
                .mapToInt(Directory::getTotalMetaDataCount)
                .sum();
        totalImages.setValue(sum);
    }

    public void addDirectory(Directory directory) {
        final ObservableList<TreeItem<Directory>> children = getRoot().getChildren();

        if (children.stream().noneMatch(i -> i.getValue().getId().equals(directory.getId()))) {
            children.add(new DirectoryTreeItem(directory));
            totalImages.add(directory.getTotalMetaDataCount());
        }
    }
}
