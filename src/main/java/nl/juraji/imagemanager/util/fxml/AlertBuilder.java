package nl.juraji.imagemanager.util.fxml;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Window;
import nl.juraji.imagemanager.util.StringUtils;

/**
 * Created by Juraji on 19-8-2018.
 * Image Manager
 */
public final class AlertBuilder {
    private final Alert alert;
    private final Text messageText;

    private AlertBuilder(Alert.AlertType alertType, Window window) {
        this.alert = new Alert(alertType);
        this.alert.initOwner(window);
        this.alert.setTitle(StringUtils.capitalize(alertType.toString()));

        this.messageText = new Text();
        this.messageText.setWrappingWidth(450);

        final BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(5));
        pane.setCenter(messageText);
        this.alert.getDialogPane().setHeader(pane);

        FXMLUtils.centerOnParent(alert, window);
    }

    public static AlertBuilder confirm(Window window) {
        return new AlertBuilder(Alert.AlertType.CONFIRMATION, window);
    }

    public static AlertBuilder info(Window window) {
        return new AlertBuilder(Alert.AlertType.INFORMATION, window);
    }

    public static AlertBuilder warning(Window window) {
        return new AlertBuilder(Alert.AlertType.WARNING, window);
    }

    public AlertBuilder withTitle(String title) {
        alert.setTitle(title);
        return this;
    }

    public AlertBuilder withMessage(String message, Object... params) {
        this.messageText.setText(String.format(message, (Object[]) params));
        return this;
    }

    public void show() {
        alert.show();
    }

    public boolean showAndWait() {
        return alert.showAndWait()
                .orElse(ButtonType.CANCEL)
                .equals(ButtonType.OK);
    }
}
