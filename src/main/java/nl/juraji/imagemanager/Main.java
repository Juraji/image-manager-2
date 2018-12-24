package nl.juraji.imagemanager;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.juraji.imagemanager.fxml.MainWindow;
import nl.juraji.imagemanager.util.fxml.Controller;
import nl.juraji.imagemanager.util.io.db.EbeanInit;

/**
 * Created by Juraji on 21-11-2018.
 * Image Manager 2
 */
public class Main extends Application {

    private static Application INSTANCE;

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    public static boolean isDebugMode() {
        return INSTANCE.getParameters().getUnnamed().contains("--debug");
    }

    @Override
    public void start(Stage stage) {
        INSTANCE = this;

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
