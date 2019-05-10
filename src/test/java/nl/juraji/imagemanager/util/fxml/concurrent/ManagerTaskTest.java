package nl.juraji.imagemanager.util.fxml.concurrent;

import org.junit.Test;
import util.IMTest;

import java.util.concurrent.CancellationException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Juraji on 9-5-2019.
 * image-manager
 */
public class ManagerTaskTest extends IMTest {

    @Test
    public void updateTaskDescription() throws Exception {
        final ManagerTask<Void> task = new ManagerTask<Void>("Test task") {
            @Override
            public Void call() {
                super.updateTaskDescription("This is a new description");
                return null;
            }
        };

        assertEquals("Test task", task.getTaskDescription());

        task.call();

        assertEquals("This is a new description", task.getTaskDescription());
    }

    @Test
    public void getTaskProgress() throws Exception {
        final ManagerTask<Void> task = new ManagerTask<Void>("Test task") {
            @Override
            public Void call() {
                this.setWorkTodo(2);
                this.setWorkDone(1);
                return null;
            }
        };

        assertEquals(-1.0, task.getTaskProgress(), 0.1);

        task.call();

        assertEquals(0.5, task.getTaskProgress(), 0.1);
    }

    @Test(expected = CancellationException.class)
    public void cancel() throws Exception {
        final ManagerTask<Void> task = new ManagerTask<Void>("Test task") {
            @Override
            public Void call() {
                super.checkIsCanceled();
                return null;
            }
        };

        task.cancel();
        task.call();
    }

    @Test
    public void updateWorkTodo() throws Exception {
        final ManagerTask<Void> task = new ManagerTask<Void>("Test task") {
            @Override
            public Void call() {
                super.setWorkTodo(6);
                return null;
            }
        };


        assertEquals(0, task.getWorkTodo());
        assertEquals(-1.0, task.getTaskProgress(), 0.1);

        task.call();

        assertEquals(6, task.getWorkTodo());
        assertEquals(0.0, task.getTaskProgress(), 0.1);
    }

    @Test
    public void updateWorkDone() throws Exception {
        final ManagerTask<Void> task = new ManagerTask<Void>("Test task") {
            @Override
            public Void call() {
                setWorkTodo(6);
                super.setWorkDone(5);
                return null;
            }
        };


        assertEquals(0, task.getWorkTodo());
        assertEquals(-1.0, task.getTaskProgress(), 0.1);

        task.call();

        assertEquals(6, task.getWorkTodo());
        assertEquals(0.83, task.getTaskProgress(), 0.01);
    }

    @Test
    public void incrementWorkDone() throws Exception {
        final ManagerTask<Void> task = new ManagerTask<Void>("Test task") {
            @Override
            public Void call() {
                this.setWorkTodo(6);
                super.incrementWorkDone();
                return null;
            }
        };


        assertEquals(0, task.getWorkTodo());
        assertEquals(-1.0, task.getTaskProgress(), 0.1);

        task.call();

        assertEquals(6, task.getWorkTodo());
        assertEquals(0.16, task.getTaskProgress(), 0.01);
    }
}
