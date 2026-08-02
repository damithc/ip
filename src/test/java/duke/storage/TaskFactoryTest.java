package duke.storage;

import java.time.LocalDateTime;

import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.Todo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests conversion from saved records to task objects.
 */
public class TaskFactoryTest {
    /** The factory used by the test cases. */
    private final TaskFactory taskFactory = new TaskFactory();

    /**
     * Verifies that an incomplete todo record creates an incomplete todo task.
     */
    @Test
    public void createFromStorageCreatesIncompleteTodo() {
        Task task = taskFactory.createFromStorage("T | 0 | read book");

        Todo todo = assertInstanceOf(Todo.class, task);
        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    /**
     * Verifies that a completed deadline record preserves its deadline and status.
     */
    @Test
    public void createFromStorageCreatesCompletedDeadline() {
        Task task = taskFactory.createFromStorage("D | 1 | return book | 2019-12-02");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getBy());
        assertTrue(deadline.isDone());
    }

    /**
     * Verifies that a stored deadline with a time preserves the typed value and formatting.
     */
    @Test
    public void createFromStorageCreatesDeadlineWithTime() {
        Task task = taskFactory.createFromStorage("D | 0 | return book | 2019-12-02 1800");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertEquals("D | 0 | return book | 2019-12-02 1800", deadline.toStorageString());
    }

    /**
     * Verifies that an event record preserves both event times.
     */
    @Test
    public void createFromStorageCreatesEvent() {
        Task task = taskFactory.createFromStorage("E | 0 | project meeting | Mon 2pm | 4pm");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
        assertFalse(event.isDone());
    }

    /**
     * Verifies that malformed records are rejected instead of creating partial tasks.
     */
    @Test
    public void createFromStorageRejectsMalformedRecords() {
        assertAll(
                () -> assertNull(taskFactory.createFromStorage("invalid record")),
                () -> assertNull(taskFactory.createFromStorage("T | 2 | invalid status")),
                () -> assertNull(taskFactory.createFromStorage("D | 0 | missing deadline | ")),
                () -> assertNull(taskFactory.createFromStorage("D | 0 | invalid date | 2019-02-30")),
                () -> assertNull(taskFactory.createFromStorage("E | 0 | missing end | 2pm | ")),
                () -> assertNull(taskFactory.createFromStorage("X | 0 | unknown type"))
        );
    }
}
