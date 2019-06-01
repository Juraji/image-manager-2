package nl.juraji.imagemanager.fxml.controls;

import javafx.scene.control.TreeItem;
import nl.juraji.imagemanager.model.domain.local.Directory;

import java.util.Set;

/**
 * Created by Juraji on 23-11-2018.
 * Image Manager 2
 */
public class DirectoryTreeItem extends TreeItem<Directory> {

    public DirectoryTreeItem(Directory directory) {
        super(directory);
        this.setExpanded(true);
        buildChildNodes(this);
    }

    public static DirectoryTreeItem root() {
        final Directory defaultDirectory = new Directory();
        defaultDirectory.setName("Root");
        return new DirectoryTreeItem(defaultDirectory);
    }

    private void buildChildNodes(DirectoryTreeItem parent) {
        final Set<Directory> children = parent.getValue().getChildren();
        if (children != null) {
            children.stream()
                    .map(DirectoryTreeItem::new)
                    .forEach(treeItem -> parent.getChildren().add(treeItem));
        }
    }
}
