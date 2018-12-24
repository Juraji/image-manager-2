package nl.juraji.imagemanager.util.types;

import com.sun.javafx.collections.ImmutableObservableList;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Created by Juraji on 2-12-2018.
 * Image Manager 2
 */
public class NullSafeBinding<T> extends ObjectBinding<T> {
    private final Supplier<T> supplier;
    private final Observable[] dependencies;

    public NullSafeBinding(Supplier<T> supplier, Observable... dependencies) {
        this.supplier = supplier;
        this.dependencies = dependencies;
        bind(dependencies);
    }

    public static <R> NullSafeBinding<R> create(Supplier<R> supplier, final Observable... dependencies) {
        return new NullSafeBinding<>(supplier, dependencies);
    }

    @Override
    protected T computeValue() {
        try {
            return supplier.get();
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).warn("Exception while evaluating binding", e);
        }

        return null;
    }

    @Override
    public void dispose() {
        unbind(dependencies);
    }

    @Override
    public ObservableList<Observable> getDependencies() {
        return ((dependencies == null) || (dependencies.length == 0)) ?
                FXCollections.emptyObservableList()
                : (dependencies.length == 1) ?
                FXCollections.singletonObservableList(dependencies[0])
                : new ImmutableObservableList<>(dependencies);
    }
}
