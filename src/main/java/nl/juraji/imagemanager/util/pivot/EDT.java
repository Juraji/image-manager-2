package nl.juraji.imagemanager.util.pivot;

import org.apache.pivot.wtk.ApplicationContext;

import java.util.function.Consumer;

/**
 * Created by Juraji on 14-5-2019.
 * image-manager
 */
public final class EDT {
    private EDT() {
        // Private constructor
    }

    public static void run(Runnable runnable) {
        ApplicationContext.queueCallback(runnable);
    }

    public static <T> Consumer<T> consume(Consumer<T> consumer) {
        return o -> ApplicationContext.queueCallback(() -> consumer.accept(o));
    }
}
