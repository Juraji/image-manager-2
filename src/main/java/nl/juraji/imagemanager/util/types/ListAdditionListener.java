package nl.juraji.imagemanager.util.types;

import javafx.collections.ListChangeListener;

import java.util.List;

/**
 * Created by Juraji on 2-12-2018.
 * Image Manager 2
 */
public interface ListAdditionListener<T> extends ListChangeListener<T> {
    @Override
    default void onChanged(Change<? extends T> c) {
        if (c.next()) {
            this.onItemsAdded(c.getAddedSubList());
        }
    }

    void onItemsAdded(List<? extends T> addedItems);
}
