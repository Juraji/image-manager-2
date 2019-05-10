package nl.juraji.imagemanager.util.fxml.concurrent;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Juraji on 9-5-2019.
 * image-manager
 */
public class ManagerTaskChain<T, R> extends ManagerTask<Void> {
    private final Collection<T> subjects;
    private final LinkedList<Function<T, ManagerTask<R>>> taskConverters;
    private final List<Consumer<R>> afterEachRunnables;
    private final List<Runnable> afterAllRunnables;

    public ManagerTaskChain(Collection<T> subjects) {
        super("");
        this.subjects = subjects;
        this.taskConverters = new LinkedList<>();
        this.afterEachRunnables = new ArrayList<>();
        this.afterAllRunnables = new ArrayList<>();
    }

    public ManagerTaskChain<T, R> nextTask(@Nullable Function<T, ManagerTask<R>> taskForSubject) {
        if (taskForSubject != null) {
            this.taskConverters.add(taskForSubject);
        }
        return this;
    }

    public ManagerTaskChain<T, R> afterEach(Consumer<R> consumer) {
        this.afterEachRunnables.add(consumer);
        return this;
    }

    public ManagerTaskChain<T, R> afterAll(Runnable runnable) {
        this.afterAllRunnables.add(runnable);
        return this;
    }

    @Override
    public Void call() throws Exception {
        if (!subjects.isEmpty()) {
            final StringProperty taskDescriptionProperty = (StringProperty) this.taskDescriptionProperty();
            final DoubleProperty taskProgressProperty = (DoubleProperty) this.taskProgressProperty();

            for (final T subject : subjects) {
                for (final Function<T, ManagerTask<R>> taskConverter : taskConverters) {
                    final ManagerTask<R> task = taskConverter.apply(subject);

                    Platform.runLater(() -> {
                        taskDescriptionProperty.unbind();
                        taskProgressProperty.unbind();

                        taskDescriptionProperty.bind(task.taskDescriptionProperty());
                        taskProgressProperty.bind(task.taskProgressProperty());
                    });

                    try {
                        final R result = task.call();
                        Platform.runLater(() -> afterEachRunnables.forEach(c -> c.accept(result)));
                        task.done(true);
                    } catch (Exception e) {
                        task.done(false);
                        throw e;
                    }
                }
            }
        }

        Platform.runLater(() -> afterAllRunnables.forEach(Runnable::run));
        return null;
    }
}
