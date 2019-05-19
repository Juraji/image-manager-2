package nl.juraji.imagemanager.util.types;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Juraji on 14-5-2019.
 * image-manager
 */
public class Property<T> {
    private T value;
    private List<Consumer<T>> changeListeners;

    public Property() {
        this(null);
    }

    public Property(T value) {
        this.value = value;
        this.changeListeners = new ArrayList<>();
    }

    public T getValue() {
        return value;
    }

    public void update(T newValue) {
        this.value = newValue;
        this.changeListeners.forEach(c -> c.accept(this.value));
    }

    public void subscribe(Consumer<T> listener) {
        this.subscribe(listener, false);
    }

    public void subscribe(Consumer<T> listener, boolean immediate) {
        this.changeListeners.add(listener);
        if(immediate && this.value != null) {
            listener.accept(this.value);
        }
    }

    public void unsubscribe(Consumer<T> listener) {
        this.changeListeners.remove(listener);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
