package nl.juraji.imagemanager.pivot.dialogs;

import nl.juraji.imagemanager.util.concurrent.ManagerTask;
import nl.juraji.imagemanager.util.concurrent.ManagerTaskListener;
import nl.juraji.imagemanager.util.concurrent.PausableExecutor;
import nl.juraji.imagemanager.util.pivot.BXMLLoader;
import nl.juraji.imagemanager.util.pivot.EDT;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.Meter;
import org.apache.pivot.wtk.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.concurrent.CancellationException;

/**
 * Created by Juraji on 14-5-2019.
 * image-manager
 */
public class TaskDialog extends Dialog implements Bindable {

    private final Logger logger;
    private final PausableExecutor executor;

    @BXML
    private Label taskDescription;
    @BXML
    private Meter meter;

    public TaskDialog() {
        this.logger = LoggerFactory.getLogger(getClass());
        this.executor = new PausableExecutor(true);
    }

    public static TaskDialog init(Window owner, String title) {
        final TaskDialog dialog = BXMLLoader.load(TaskDialog.class);
        dialog.open(owner);
        dialog.setTitle(title);
        return dialog;
    }

    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
        // The executor is waiting to be resumed
        this.executor.resume();
    }

    public <T> TaskDialog thenRun(ManagerTask<T> task) {
        return thenRun(task, result -> {
            // Do nothing
        });
    }

    public <T> TaskDialog thenRun(ManagerTask<T> task, ManagerTaskListener<T> listener) {
        this.executor.submit(() -> {
            try {
                task.getTaskDescription().subscribe(EDT.consume(this.taskDescription::setText), true);
                task.getTaskProgress().subscribe(EDT.consume(this.meter::setPercentage), true);

                final T result = task.call();
                EDT.consume(listener::onSuccess).accept(result);

                task.done(true);

                logger.info("Completed task \"{}\"", task.getTaskDescription());
            } catch (CancellationException e) {
                logger.info("Task \"{}\" canceled", task.getTaskDescription());
                task.done(false);
                EDT.run(listener::onCanceled);
            } catch (Exception e) {
                logger.error("Error executing task \"" + task.getTaskDescription() + "\"", e);
                task.done(false);
                EDT.consume(listener::onFailed).accept(e);
            }
        });

        return this;
    }

    public void done() {
        done(null);
    }

    public void done(Runnable afterAll) {
        this.executor.submit(() -> {
            if (afterAll != null) {
                EDT.run(afterAll);
            }

            EDT.run(this::close);
            this.executor.shutdown();
        });
    }
}
