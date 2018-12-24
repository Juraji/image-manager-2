package nl.juraji.imagemanager.util.fxml.concurrent;

import javafx.application.Platform;

/**
 * Created by Juraji on 9-12-2018.
 * Image Manager 2
 */
public final class SimpleIndicatorTask extends IndicatorTask<Void> {
    private final Runnable task;
    private final boolean runOnFXThread;

    public SimpleIndicatorTask(Runnable task) {
        this(task, true);
    }

    public SimpleIndicatorTask(Runnable task, boolean runOnFXThread) {
        super(task.toString());
        this.task = task;
        this.runOnFXThread = runOnFXThread;
    }

    @Override
    protected Void call() {
        if (runOnFXThread) {
            Platform.runLater(task);
        } else {
            task.run();
        }
        return null;
    }
}
