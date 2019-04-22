package nl.juraji.imagemanager;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.juraji.imagemanager.fxml.MainWindow;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.io.db.EbeanInit;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Juraji on 21-11-2018.
 * Image Manager 2
 */
public class Main extends Application {
    private static final AtomicReference<Main> INSTANCE = new AtomicReference<>();

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    public static boolean isDebugMode() {
        return INSTANCE.get().getParameters().getUnnamed().contains("--debug");
    }

    @Override
    public void start(Stage stage) {
        INSTANCE.set(this);

        // Setup database
        EbeanInit.init();

        // Setup primary stage with root scene and load
        Controller.initStage(MainWindow.class, "Image manager", stage);
        stage.show();
    }

    public static void exit() {
        System.exit(0);
    }
}
