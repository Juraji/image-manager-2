package nl.juraji.imagemanager.fxml.dialogs;

import com.google.common.util.concurrent.AtomicDouble;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Window;
import nl.juraji.imagemanager.util.fxml.concurrent.IndicatorTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Created by Juraji on 25-11-2018.
 * Image Manager 2
 */
public class WorkQueueDialog<T> extends WorkDialog<T> implements Initializable {
    private final AtomicDouble taskCount;
    private final BlockingQueue<IndicatorTask<T>> taskQueue;
    private final AtomicBoolean executionInProgress;
    private final List<T> queueResults;
    private final List<Consumer<List<T>>> afterAll;

    @FXML
    private ProgressIndicator tasksProgressBar;

    public WorkQueueDialog(Window owner) {
        super(owner);
        this.taskCount = new AtomicDouble();
        this.taskQueue = new LinkedBlockingQueue<>();
        this.executionInProgress = new AtomicBoolean(false);
        this.queueResults = new ArrayList<>();
        this.afterAll = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        this.tasksProgressBar.setVisible(true);
    }

    @Override
    public void exec(IndicatorTask<T> task, boolean closeOnCompletion) {
        throw new UnsupportedOperationException("Direct execution is not supported by WorkQueueDialog, " +
                "use #queue then #execute or use WorkDialog");
    }

    public void queue(IndicatorTask<T> task) {
        taskQueue.add(task);
        taskCount.set(taskQueue.size());
        logger.info("Queued task \"{}\"", task.getMessage());
    }

    public void addQueueEndNotification(Runnable runnable) {
        this.addQueueEndNotification(l -> runnable.run());
    }

    public void addQueueEndNotification(Consumer<List<T>> runnable) {
        this.afterAll.add(runnable);
    }

    public void execute() {
        if (executionInProgress.get()) {
            throw new IllegalStateException("Execution was already in progress");
        }

        final Thread thread = new Thread(() -> {
            logger.info("Start dequeuing and executing tasks...");

            while (taskQueue.size() > 0) {
                final double tasksProgress = (taskCount.get() - taskQueue.size()) / taskCount.get();
                tasksProgressBar.setProgress(tasksProgress);

                try {
                    // Take from queue
                    final IndicatorTask<T> queueTask = taskQueue.take();

                    // Start execution from java fx thread
                    Platform.runLater(() -> super.exec(queueTask, taskQueue.size() == 0));

                    // Wait for task to complete
                    queueResults.add(queueTask.get());
                } catch (Exception e) {
                    if (e instanceof CancellationException) {
                        // Queue was canceled, clear queue and break loop
                        logger.info("Canceled tasks");
                        taskQueue.clear();
                        Platform.runLater(this::hide);
                        break;
                    } else {
                        logger.error("Error executing task", e);
                    }
                }
            }

            // Run after all
            final ArrayList<T> resultsForLater = new ArrayList<>(queueResults);
            Platform.runLater(() -> afterAll.forEach(c -> c.accept(resultsForLater)));
            executionInProgress.set(false);
            queueResults.clear();
        });

        executionInProgress.set(true);
        thread.start();
    }
}
