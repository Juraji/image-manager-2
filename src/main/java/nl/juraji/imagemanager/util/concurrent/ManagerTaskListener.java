package nl.juraji.imagemanager.util.concurrent;

/**
 * Created by Juraji on 14-5-2019.
 * image-manager
 */
public interface ManagerTaskListener<T> {

    void onSuccess(T result);

    default void onFailed(Exception e) {
        // Do nothing
    }

    default void onCanceled() {
        // Do nothing
    }
}
