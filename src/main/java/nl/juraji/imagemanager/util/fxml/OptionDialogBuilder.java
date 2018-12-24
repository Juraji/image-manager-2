package nl.juraji.imagemanager.util.fxml;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public final class OptionDialogBuilder {
    private final Alert alert;
    private final List<ButtonType> options;
    private final Text messageText;

    private OptionDialogBuilder(Window window) {
        this.options = new ArrayList<>();
        this.alert = new Alert(Alert.AlertType.CONFIRMATION);

        this.alert.initModality(Modality.WINDOW_MODAL);
        this.alert.initOwner(window);

        if (window instanceof Stage) {
            ((Stage) this.alert.getDialogPane().getScene().getWindow())
                    .getIcons().add(FXMLUtils.getApplicationIcon());
        }

        this.messageText = new Text();
        this.messageText.setWrappingWidth(450);

        final BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(5));
        pane.setCenter(messageText);
        this.alert.getDialogPane().setHeader(pane);

        FXMLUtils.centerOnParent(alert, window);
    }

    public static OptionDialogBuilder build(Window window) {
        return new OptionDialogBuilder(window);
    }

    public OptionDialogBuilder withTitle(String title) {
        this.alert.setTitle(title);
        return this;
    }

    public OptionDialogBuilder withMessage(String message, Object... params) {
        this.messageText.setText(String.format(message, (Object[]) params));
        return this;
    }

    public OptionDialogBuilder withOption(String optionText) {
        this.options.add(new ButtonType(optionText));
        return this;
    }

    public int show() {
        this.alert.getButtonTypes().setAll(options);
        this.alert.getButtonTypes().add(ButtonType.CANCEL);

        final ButtonType resultType = this.alert.showAndWait().orElse(ButtonType.CANCEL);

        if (resultType == ButtonType.CANCEL) {
            return -1;
        } else {
            return this.options.indexOf(resultType);
        }
    }
}
