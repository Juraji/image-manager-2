package nl.juraji.imagemanager.fxml.dialogs;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import nl.juraji.imagemanager.util.exceptions.ImageManagerError;
import nl.juraji.imagemanager.util.fxml.FXMLUtils;
import nl.juraji.imagemanager.util.fxml.concurrent.ManagerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Created by Juraji on 25-11-2018.
 * Image Manager 2
 */
public class WorkDialog<T> extends BorderPane implements Initializable {
    private final Stage dialog;
    private final ObservableList<T> resultNotificationList;
    private final AtomicReference<ManagerTask<T>> currentTask;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @FXML
    private ProgressIndicator currentTaskProgressBar;

    @FXML
    private Label messageLabel;

    @FXML
    private Button cancelTaskButton;

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    public WorkDialog(Window owner) {
        try {
            this.resultNotificationList = FXCollections.observableArrayList();
            this.currentTask = new AtomicReference<>();

            this.dialog = new Stage(StageStyle.UNDECORATED);
            this.dialog.initModality(Modality.WINDOW_MODAL);
            this.dialog.initOwner(owner);
            this.dialog.setResizable(false);
            FXMLUtils.centerOnParent(dialog, owner);

            final FXMLLoader loader = new FXMLLoader(WorkDialog.class
                    .getResource("/nl/juraji/imagemanager/fxml/dialogs/WorkDialog.fxml"));

            loader.setRoot(this);
            loader.setController(this);

            final Parent parent = loader.load();
            final Scene scene = new Scene(parent);

            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), this::cancelCurrentTask);

            this.dialog.setScene(scene);
        } catch (IOException e) {
            throw new ImageManagerError("Failed setting up work dialog", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelTaskButton.setOnAction(e -> this.cancelCurrentTask());
    }

    public void addTaskEndNotification(Consumer<T> event) {
        // Will call consumer for result after every execution
        this.resultNotificationList.addListener((ListChangeListener<T>) n -> {
            if (n.next()) {
                n.getAddedSubList().forEach(event);
            }
        });
    }

    public void cancelCurrentTask() {
        final ManagerTask<T> task = currentTask.get();
        if (task != null) {
            task.cancel();
            currentTask.set(null);
        }
    }

    public void exec(ManagerTask<T> task) {
        this.exec(task, true);
    }

    public void exec(ManagerTask<T> task, boolean closeOnCompletion) {
        logger.info("Executing task \"{}\"", task.getTaskDescription());

        // Set current task
        this.currentTask.set(task);

        // Show dialog and bind it to the task
        this.dialog.show();

        // Bind progressbar to the current task
        this.currentTaskProgressBar.progressProperty().unbind();
        this.currentTaskProgressBar.setProgress(-1);
        this.currentTaskProgressBar.progressProperty().bind(task.taskProgressProperty());

        // Bind message to the current task
        this.messageLabel.textProperty().unbind();
        this.messageLabel.textProperty().set(null);
        this.messageLabel.textProperty().bind(task.taskDescriptionProperty());

        this.executorService.submit(() -> {
            try {
                final T result = task.call();
                Platform.runLater(() -> this.resultNotificationList.add(result));

                task.done(true);

                logger.info("Completed task \"{}\"", task.getTaskDescription());
            } catch (CancellationException e) {
                logger.info("Task \"{}\" canceled", task.getTaskDescription());
                task.done(false);
            } catch (Exception e) {
                logger.error("Error executing task \"" + task.getTaskDescription() + "\"", e);
                task.done(false);
            } finally {
                this.currentTask.set(null);
                this.messageLabel.textProperty().unbind();
                this.currentTaskProgressBar.progressProperty().unbind();

                if (closeOnCompletion) {
                    Platform.runLater(this.dialog::close);
                }
            }
        });
    }
}
