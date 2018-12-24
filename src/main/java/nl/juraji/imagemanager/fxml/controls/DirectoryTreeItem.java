package nl.juraji.imagemanager.fxml.controls;

import javafx.scene.control.TreeItem;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.DefaultDirectory;

import java.util.Set;

/**
 * Created by Juraji on 23-11-2018.
 * Image Manager 2
 */
public class DirectoryTreeItem extends TreeItem<BaseDirectory> {

    public DirectoryTreeItem(BaseDirectory directory) {
        super(directory);
        this.setExpanded(true);
        buildChildNodes(this);
    }

    public static DirectoryTreeItem root() {
        final DefaultDirectory defaultDirectory = new DefaultDirectory();
        defaultDirectory.setName("Root");
        return new DirectoryTreeItem(defaultDirectory);
    }

    @SuppressWarnings("unchecked")
    private void buildChildNodes(DirectoryTreeItem parent) {
        final Set<BaseDirectory> children = parent.getValue().getChildren();
        if (children != null) {
            children.stream()
                    .map(DirectoryTreeItem::new)
                    .forEach(treeItem -> parent.getChildren().add(treeItem));
        }
    }
}
