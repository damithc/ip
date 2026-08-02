package duke.service;

import java.nio.file.Path;

import duke.exception.DamienException;
import duke.storage.Storage;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.Todo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests task changes and persistence coordinated by {@link TaskService}.
 */
public class TaskServiceTest {
    /** Temporary directory used for the service's storage files. */
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that adding a task updates the service and persists it.
     *
     * @throws DamienException if the task cannot be saved
     */
    @Test
    public void addUpdatesTaskListAndPersistsTask() throws DamienException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskService service = new TaskService(tasks, storage);
        Task task = new Todo("read book");

        service.add(task);

        assertSame(tasks, service.getTasks());
        assertEquals(1, service.size());
        assertSame(task, service.getTasks().get(0));
        assertEquals(1, storage.load().size());
    }

    /**
     * Verifies that marking and unmarking a task update and persist its status.
     *
     * @throws DamienException if the task cannot be saved
     */
    @Test
    public void markAndUnmarkUpdateTaskStatusAndPersistence() throws DamienException {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.add(task);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskService service = new TaskService(tasks, storage);

        assertSame(task, service.markAsDone(0));
        assertTrue(task.isDone());
        assertSame(task, service.unmark(0));
        assertFalse(task.isDone());
        assertFalse(storage.load().get(0).isDone());
    }

    /**
     * Verifies that deleting a task removes and persists the selected task.
     *
     * @throws DamienException if the task cannot be saved
     */
    @Test
    public void deleteRemovesTaskAndPersistsRemainingTasks() throws DamienException {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        tasks.add(firstTask);
        tasks.add(secondTask);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskService service = new TaskService(tasks, storage);

        assertSame(firstTask, service.delete(0));

        assertEquals(1, service.size());
        assertSame(secondTask, service.getTasks().get(0));
        assertEquals(1, storage.load().size());
        assertEquals("return book", storage.load().get(0).getDescription());
    }

    /**
     * Verifies that an invalid task index is rejected without changing the list.
     */
    @Test
    public void invalidTaskIndexProducesDamienException() {
        TaskService service = new TaskService(new TaskList(),
                new Storage(temporaryDirectory.resolve("tasks.txt")));

        DamienException exception = assertThrows(DamienException.class,
                () -> service.delete(0));

        assertEquals("Task 1 does not exist. Use list to see valid task numbers.",
                exception.getMessage());
        assertEquals(0, service.size());
    }
}
