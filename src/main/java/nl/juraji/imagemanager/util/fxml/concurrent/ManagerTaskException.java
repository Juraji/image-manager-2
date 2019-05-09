package nl.juraji.imagemanager.util.fxml.concurrent;

/**
 * Created by Juraji on 9-5-2019.
 * image-manager
 */
public class ManagerTaskException extends Exception {
    public ManagerTaskException() {
    }

    public ManagerTaskException(String message) {
        super(message);
    }

    public ManagerTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerTaskException(Throwable cause) {
        super(cause);
    }
}
