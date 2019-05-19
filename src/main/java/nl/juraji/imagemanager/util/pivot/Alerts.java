package nl.juraji.imagemanager.util.pivot;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Window;

import java.util.function.Consumer;

/**
 * Created by Juraji on 14-5-2019.
 * image-manager
 */
public final class Alerts {

    private static final List<String> QUESTION_OPTIONS = new ArrayList<>("Yes", "No");
    private static final List<String> MESSAGE_OPTIONS = new ArrayList<>("Ok");

    private Alerts() {
        // Private constructor
    }

    public static void question(Window owner, String message, Runnable onYes) {
        question(owner, message, result -> {
            if (result) {
                onYes.run();
            }
        });
    }

    public static void question(Window owner, String message, Consumer<Boolean> onAction) {
        final Alert alert = new Alert(MessageType.QUESTION, message, QUESTION_OPTIONS);

        alert.open(owner, (dialog, modal) -> {
            if (dialog.getResult()) {
                onAction.accept(QUESTION_OPTIONS.get(0).equals(alert.getSelectedOption()));
            }
        });
    }

    public static void multiOptionQuestion(Window owner, String message, String[] options, Consumer<String> onAction) {
        final Alert alert = new Alert(MessageType.QUESTION, message, new ArrayList<>(options));

        alert.open(owner, (dialog, modal) -> {
            if (dialog.getResult()) {
                onAction.accept((String) alert.getSelectedOption());
            }
        });
    }

    public static void warn(Window owner, String message) {
        message(owner, MessageType.WARNING, message);
    }

    public static void error(Window owner, String message) {
        message(owner, MessageType.ERROR, message);
    }

    public static void message(Window owner, MessageType messageType, String message) {
        new Alert(messageType, message, MESSAGE_OPTIONS)
                .open(owner);
    }
}
