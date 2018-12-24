package nl.juraji.imagemanager.util.fxml;

import javafx.stage.Stage;

/**
 * Created by Juraji on 29-11-2018.
 * Image Manager 2
 */
public final class FXMLStage<T> {
    private final T controller;
    private final Stage stage;

    FXMLStage(T controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public T getController() {
        return controller;
    }

    public Stage getStage() {
        return stage;
    }

    public void show() {
        stage.show();
    }

    public void onClose(Runnable o) {
        this.stage.setOnHiding(e -> o.run());
    }
}
