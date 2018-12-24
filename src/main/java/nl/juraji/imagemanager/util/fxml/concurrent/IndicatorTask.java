package nl.juraji.imagemanager.util.fxml.concurrent;

import javafx.concurrent.Task;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Juraji on 27-11-2018.
 * Image Manager 2
 */
public abstract class IndicatorTask<T> extends Task<T> {
    private final AtomicLong workDone = new AtomicLong(0);
    private final AtomicLong totalWork = new AtomicLong(0);

    public IndicatorTask(String message, Object... params) {
        this.updateMessage(String.format(message, (Object[]) params));
    }

    protected void setTotalWork(long totalWork) {
        this.totalWork.set(totalWork);
        updateProgress(this.workDone.get(), this.totalWork.get());
    }

    protected void addToTotalWork(long work) {
        final long l = this.totalWork.addAndGet(work);
        updateProgress(this.workDone.get(), l);
    }

    protected void updateMessage(String message, Object... params) {
        super.updateMessage(String.format(message, (Object[]) params));
    }

    @Override
    protected void updateProgress(long workDone, long max) {
        this.workDone.set(workDone);
        this.totalWork.set(max);
        super.updateProgress(workDone, max);
    }

    @Override
    protected void updateProgress(double workDone, double max) {
        this.workDone.set((long) workDone);
        this.totalWork.set((long) max);
        super.updateProgress(workDone, max);
    }

    protected void setProgress(long workDone) {
        this.workDone.set(workDone);
        super.updateProgress(workDone, totalWork.get());
    }

    protected void incrementProgress() {
        final long l = this.workDone.addAndGet(1);
        super.updateProgress(l, this.totalWork.get());
    }

    protected void checkCanceled() {
        if (isCancelled()) {
            throw new CancellationException("Task canceled");
        }
    }
}
