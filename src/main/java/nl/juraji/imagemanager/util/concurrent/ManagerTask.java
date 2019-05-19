package nl.juraji.imagemanager.util.concurrent;

import nl.juraji.imagemanager.util.types.Property;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Juraji on 9-5-2019.
 * image-manager
 */
public abstract class ManagerTask<T> implements Callable<T> {
    private final Property<String> taskDescription;
    private final Property<Double> taskProgress;
    private final AtomicLong workDone;
    private final AtomicLong workTodo;
    private final AtomicBoolean isCanceled;

    public Property<String> getTaskDescription() {
        return taskDescription;
    }

    public Property<Double> getTaskProgress() {
        return taskProgress;
    }

    public long getWorkDone() {
        return workDone.get();
    }

    public long getWorkTodo() {
        return workTodo.get();
    }

    public ManagerTask(String taskDescription, Object... args) {
        this.taskDescription = new Property<>(String.format(taskDescription, (Object[]) args));
        this.taskProgress = new Property<>(0.0);
        this.workDone = new AtomicLong(0);
        this.workTodo = new AtomicLong(0);
        this.isCanceled = new AtomicBoolean(false);
    }

    public void done(boolean success) {
        // Do nothing by default
    }

    public void cancel() {
        this.isCanceled.set(true);
    }

    protected void updateTaskDescription(final String description, final Object... args) {
        taskDescription.update(String.format(description, (Object[]) args));
    }

    protected void setWorkTodo(long workTodo) {
        this.workTodo.set(workTodo);
        this.updateTaskProgress();
    }

    protected void addWorkTodo(long delta) {
        this.workTodo.addAndGet(delta);
        this.updateTaskProgress();
    }

    protected void setWorkDone(long workDone) {
        this.workDone.set(workDone);
        this.updateTaskProgress();
    }

    protected void incrementWorkDone() {
        this.workDone.addAndGet(1);
        this.updateTaskProgress();
    }

    protected void checkIsCanceled() {
        if (this.isCanceled.get()) {
            throw new CancellationException("Task canceled");
        }
    }

    private void updateTaskProgress() {
        double done = (double) workDone.get();
        double todo = (double) workTodo.get();
        this.taskProgress.update(done / todo);
    }
}
