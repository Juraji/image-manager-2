package util;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Juraji on 8-5-2019.
 * image-manager
 */
public class FXInit extends Application {
    private static final CountDownLatch LATCH = new CountDownLatch(1);
    private static final AtomicBoolean WAS_INITIALIZED = new AtomicBoolean(false);

    public static void initializeFXToolkit() throws InterruptedException {
        if(!WAS_INITIALIZED.get()) {
            new Thread(() -> Application.launch(FXInit.class)).start();
            WAS_INITIALIZED.set(true);
            LATCH.await();
        }
    }

    @Override
    public void init() {
        LATCH.countDown();
    }

    @Override
    public void start(Stage primaryStage) {
    }
}
