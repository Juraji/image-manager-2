package nl.juraji.imagemanager.util.fxml;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Created by Juraji on 1-12-2018.
 * Image Manager 2
 */
public abstract class Controller {
    private Stage stage;
    private Runnable onClose;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        if (this.onClose != null) {
            this.onClose.run();
        }
        stage.hide();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public static <T extends Controller> FXMLStage<T> initStage(Class<T> controllerClass, String title, Stage stage) {
        try {
            final String fxmlName = '/' + controllerClass.getName()
                    .replaceAll("\\.", "/") + ".fxml";
            final FXMLLoader loader = new FXMLLoader(FXMLUtils.class.getResource(fxmlName));

            Parent node = loader.load();
            T controller = loader.getController();

            stage.setScene(new Scene(node));
            stage.setTitle(title);
            stage.getIcons().add(FXMLUtils.getApplicationIcon());

            controller.setStage(stage);

            return new FXMLStage<>(controller, stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Controller> FXMLStage<T> init(Class<T> controllerClass, String title, Window owner) {
        final FXMLStage<T> fxmlStage = initStage(controllerClass, title, new Stage());
        final Stage stage = fxmlStage.getStage();

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        FXMLUtils.centerOnParent(stage, owner);

        return fxmlStage;
    }
}
