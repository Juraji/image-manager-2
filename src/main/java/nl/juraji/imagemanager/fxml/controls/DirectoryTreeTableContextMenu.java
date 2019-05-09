package nl.juraji.imagemanager.fxml.controls;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import nl.juraji.imagemanager.fxml.DirectoryMetaDataWindow;
import nl.juraji.imagemanager.fxml.DuplicateScannerWindow;
import nl.juraji.imagemanager.model.domain.BaseDirectory;
import nl.juraji.imagemanager.model.domain.pinterest.PinterestBoard;
import nl.juraji.imagemanager.tasks.IndexDirectoryTaskBuilder;
import nl.juraji.imagemanager.util.DesktopUtils;
import nl.juraji.imagemanager.util.FileUtils;
import nl.juraji.imagemanager.util.fxml.AlertBuilder;
import nl.juraji.imagemanager.util.fxml.ContextMenuBuilder;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.fxml.FXMLStage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public class DirectoryTreeTableContextMenu extends ContextMenu {

    private final DirectoryTreeTableView parentTableView;

    public DirectoryTreeTableContextMenu(DirectoryTreeTableView parentTableView) {
        this.parentTableView = parentTableView;

        final ObservableList<MenuItem> items = this.getItems();
        items.clear();

        final List<MenuItem> menuItems = ContextMenuBuilder.build(null)
                .appendItem("View content", this::viewContentAction)
                .appendSeparator()
                .appendItem("Index selected directories", this::indexSelectedDirectoriesAction)
                .appendItem("Scan for duplicates", this::scanForDuplicatesAction)
                .appendSeparator()
                .appendItem("Open in explorer", this::openInExplorerAction)
                .appendItem("Open in Pinterest", this::openInPinterestAction)
                .appendSeparator()
                .appendItem("Toggle favorite", this::toggleFavoriteAction)
                .appendSeparator()
                .appendItem("Delete", this::deleteAction)
                .appendItem("Delete from disk", this::deleteFromDiskAction)
                .getItems();

        items.addAll(menuItems);
    }

    // Actions
    private void viewContentAction() {
        final TreeItem<BaseDirectory> selectedItem = getSelectedItem();

        if (selectedItem != null) {
            final BaseDirectory directory = selectedItem.getValue();
            final FXMLStage<DirectoryMetaDataWindow> fxmlStage = Controller.init(DirectoryMetaDataWindow.class,
                    "Directory - " + directory.getName(), getOwnerWindow());

            final DirectoryMetaDataWindow controller = fxmlStage.getController();
            controller.setDirectory(directory);

            fxmlStage.show();
            fxmlStage.onClose(parentTableView::loadDirectoryTrees);
        }

    }

    private void indexSelectedDirectoriesAction() {
        final List<TreeItem<BaseDirectory>> directories = reduceSelectionToParents();
        if (!directories.isEmpty()) {
            final List<BaseDirectory> directories1 = directories.stream()
                    .map(TreeItem::getValue)
                    .collect(Collectors.toList());

            IndexDirectoryTaskBuilder.standard(getOwnerWindow(), directories1)
                    .afterEach(o -> parentTableView.refresh())
                    .runIndex();
        }
    }

    private void scanForDuplicatesAction() {
        final List<TreeItem<BaseDirectory>> selection = reduceSelectionToParents();

        if (!selection.isEmpty()) {
            final FXMLStage<DuplicateScannerWindow> fxmlStage = Controller.init(DuplicateScannerWindow.class,
                    "Image Manager - Duplicate Scanner", getOwnerWindow());

            final DuplicateScannerWindow controller = fxmlStage.getController();

            fxmlStage.onClose(parentTableView::loadDirectoryTrees);
            fxmlStage.show();

            controller.scan(selection.stream()
                    .map(TreeItem::getValue)
                    .collect(Collectors.toList()));
        }
    }

    private void openInExplorerAction() {
        final TreeItem<BaseDirectory> item = getSelectedItem();
        if (item != null) {
            DesktopUtils.openFile(item.getValue().getLocationOnDisk());
        }
    }

    private void openInPinterestAction() {
        final TreeItem<BaseDirectory> item = getSelectedItem();
        if (item != null) {
            final BaseDirectory directory = item.getValue();
            if (directory instanceof PinterestBoard) {
                DesktopUtils.openWebUri(((PinterestBoard) directory).getBoardUrl());
            }
        }
    }

    private void toggleFavoriteAction() {
        final List<TreeItem<BaseDirectory>> selection = getSelectedItems();
        selection.stream()
                .map(TreeItem::getValue)
                .forEach(directory -> {
                    directory.setFavorite(!directory.isFavorite());
                    directory.save();


                });
        parentTableView.refresh();
    }

    private void deleteAction() {
        this.deleteActionImpl(false);
    }

    private void deleteFromDiskAction() {
        this.deleteActionImpl(true);
    }

    private void deleteActionImpl(boolean deleteFromDisk) {
        final List<TreeItem<BaseDirectory>> selection = reduceSelectionToParents();

        if (!selection.isEmpty()) {
            final boolean doDelete = AlertBuilder.confirm(getOwnerWindow())
                    .withTitle("Delete directory")
                    .withMessage("Are you sure you want to delete the %s selected from %sImage Manager's index?\n" +
                                    "This action can not be undone!",
                            selection.size(),
                            (deleteFromDisk ? "disk and " : ""))
                    .showAndWait();

            if (doDelete) {
                try {
                    for (TreeItem<BaseDirectory> item : selection) {
                        final BaseDirectory directory = item.getValue();
                        if (deleteFromDisk) {
                            FileUtils.delete(directory.getLocationOnDisk());
                        }

                        directory.delete();
                        item.getParent().getChildren().remove(item);
                    }
                } catch (IOException e) {
                    AlertBuilder.warning(getOwnerWindow())
                            .withTitle("Deletion failed")
                            .withMessage("Failed deleting {}, make sure you have the correct access rights!");
                }

                parentTableView.getSelectionModel().clearSelection();
                parentTableView.refresh();
            }
        }
    }

    /**
     * Get all selected items.
     */
    private List<TreeItem<BaseDirectory>> getSelectedItems() {
        return parentTableView.getSelectionModel().getSelectedItems().stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get last selected item.
     */
    private TreeItem<BaseDirectory> getSelectedItem() {
        final TreeTableView.TreeTableViewSelectionModel<BaseDirectory> selectionModel = parentTableView.getSelectionModel();
        final int selectedIndex = selectionModel.getSelectedIndex();
        selectionModel.clearSelection();
        selectionModel.select(selectedIndex);
        return selectionModel.getSelectedItem();
    }

    /**
     * Get all selected items.
     * If a child and its parent are selected the child gets deselected.
     */
    private List<TreeItem<BaseDirectory>> reduceSelectionToParents() {
        final TreeTableView.TreeTableViewSelectionModel<BaseDirectory> selectionModel = parentTableView.getSelectionModel();
        final List<TreeItem<BaseDirectory>> selection = new ArrayList<>(getSelectedItems());

        selectionModel.clearSelection();
        return selection.stream()
                .filter(Objects::nonNull)
                .filter(item -> selection.stream().noneMatch(i -> isParent(item, i)))
                .peek(selectionModel::select)
                .collect(Collectors.toList());
    }

    private boolean isParent(TreeItem child, TreeItem sibling) {
        if (child.equals(sibling)) {
            return false;
        } else {
            final TreeItem parent = child.getParent();
            return parent != null
                    && (parent.equals(sibling) || isParent(parent, sibling));
        }
    }
}
