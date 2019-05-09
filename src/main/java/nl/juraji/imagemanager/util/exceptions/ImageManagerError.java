package nl.juraji.imagemanager.util.exceptions;

/**
 * Created by Juraji on 9-5-2019.
 * image-manager
 */
public class ImageManagerError extends RuntimeException {
    public ImageManagerError() {
    }

    public ImageManagerError(String message) {
        super(message);
    }

    public ImageManagerError(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageManagerError(Throwable cause) {
        super(cause);
    }

    public ImageManagerError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
