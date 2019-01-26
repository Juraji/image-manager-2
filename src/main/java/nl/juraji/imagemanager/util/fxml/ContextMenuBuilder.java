package nl.juraji.imagemanager.util.fxml;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Juraji on 24-11-2018.
 * Image Manager 2
 */
public final class ContextMenuBuilder<T> {
    private final ContextMenu contextMenu;
    private final T subject;

    private ContextMenuBuilder(T subject) {
        this.contextMenu = new ContextMenu();
        this.subject = subject;
    }

    public static <R> ContextMenuBuilder<R> build(R subject) {
        return new ContextMenuBuilder<>(subject);
    }

    /**
     * Append a menu item with post event runnable
     *
     * @param label         Menu item label
     * @param onActionEvent Action when menu item activated
     * @return this
     */
    public ContextMenuBuilder<T> appendItem(String label, Runnable onActionEvent) {
        return appendItem(label, t -> onActionEvent.run());
    }

    /**
     * Append a menu item with post event runnable
     *
     * @param label         Menu item label
     * @param onActionEvent Action when menu item activated
     * @param disable       When true this action will be disabled
     * @return this
     */
    public ContextMenuBuilder<T> appendItem(String label, Runnable onActionEvent, BooleanBinding disable) {
        return appendItem(label, t -> onActionEvent.run(), disable);
    }

    /**
     * Append a menu item with post event runnable
     *
     * @param label         Menu item label
     * @param onActionEvent Action when menu item activated
     * @return this
     */
    public ContextMenuBuilder<T> appendItem(String label, Consumer<T> onActionEvent) {
        return appendItem(label, onActionEvent, s -> false);
    }

    /**
     * Append a menu item with post event runnable
     *
     * @param label         Menu item label
     * @param onActionEvent Action when menu item activated
     * @param disable       When true this action will be disabled
     * @return this
     */
    public ContextMenuBuilder<T> appendItem(String label, Consumer<T> onActionEvent, Predicate<T> disable) {
        MenuItem menuItem = new MenuItem(label);
        menuItem.setOnAction(e -> onActionEvent.accept(subject));
        menuItem.setDisable(disable.test(subject));

        this.contextMenu.getItems().add(menuItem);
        return this;
    }

    /**
     * Append a menu item with post event runnable
     *
     * @param label         Menu item label
     * @param onActionEvent Action when menu item activated
     * @param disable       When true this action will be disabled
     * @return this
     */
    public ContextMenuBuilder<T> appendItem(String label, Consumer<T> onActionEvent, BooleanBinding disable) {
        MenuItem menuItem = new MenuItem(label);
        menuItem.setOnAction(e -> onActionEvent.accept(subject));
        menuItem.disableProperty().bind(disable);

        this.contextMenu.getItems().add(menuItem);
        return this;
    }

    /**
     * Append a separator
     *
     * @return this
     */
    public ContextMenuBuilder<T> appendSeparator() {
        this.contextMenu.getItems().add(new SeparatorMenuItem());
        return this;
    }

    /**
     * Set consumer to run when context menu is showing
     *
     * @param consumer Consumer accepting T
     * @return this
     */
    public ContextMenuBuilder<T> setOnShowing(Consumer<T> consumer) {
        this.contextMenu.setOnShowing(e -> consumer.accept(this.subject));
        return this;
    }

    /**
     * Set consumer to run when context menu is hiding
     *
     * @param consumer Consumer accepting T
     * @return this
     */
    public ContextMenuBuilder<T> setOnHiding(Consumer<T> consumer) {
        this.contextMenu.setOnHiding(e -> consumer.accept(this.subject));
        return this;
    }

    /**
     * Bind this context menu to a Node
     *
     * @param node The node to bind to
     */
    public void bindTo(Node node) {
        node.setOnContextMenuRequested(e ->
                contextMenu.show(node, e.getScreenX(), e.getScreenY()));
    }

    public List<MenuItem> getItems() {
        return contextMenu.getItems();
    }
}
