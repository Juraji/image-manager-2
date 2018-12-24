package nl.juraji.imagemanager.util.types;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public interface ValueListener<T> extends ChangeListener<T> {

    @Override
    default void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        this.changed(newValue);
    }

    void changed(T newValue);
}
