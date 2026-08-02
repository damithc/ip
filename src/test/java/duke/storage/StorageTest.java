package duke.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import duke.exception.DamienException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.TaskList;
import duke.task.Todo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests saving, loading, and corruption tracking in {@link Storage}.
 */
public class StorageTest {
    /** Temporary directory used so tests do not modify the project's data file. */
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that saving and loading preserves task types and completion status.
     *
     * @throws DamienException if the temporary task file cannot be read or written
     */
    @Test
    public void saveAndLoadPreservesTasks() throws DamienException {
        Path file = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(file);
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        deadline.markAsDone();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(todo.toStorageString(), loadedTasks.get(0).toStorageString());
        assertEquals(deadline.toStorageString(), loadedTasks.get(1).toStorageString());
        assertEquals(event.toStorageString(), loadedTasks.get(2).toStorageString());
        assertEquals(0, storage.getCorruptedRecordCount());
    }

    /**
     * Verifies that loading a missing file starts with an empty task list.
     *
     * @throws DamienException if the missing task file cannot be handled
     */
    @Test
    public void loadMissingFileReturnsEmptyTaskList() throws DamienException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/tasks.txt"));

        TaskList loadedTasks = storage.load();

        assertEquals(0, loadedTasks.size());
        assertEquals(0, storage.getCorruptedRecordCount());
    }

    /**
     * Verifies that valid records load while malformed records are counted and skipped.
     *
     * @throws IOException if the temporary records cannot be written
     * @throws DamienException if the records cannot be loaded
     */
    @Test
    public void loadSkipsMalformedRecordsAndCountsThem() throws IOException, DamienException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "T | 0 | keep this task",
                "invalid record",
                "D | 1 | return book | 2019-12-02",
                "E | 0 | project meeting | Mon 2pm | 4pm",
                "T | 0 | ") + System.lineSeparator());
        Storage storage = new Storage(file);

        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(2, storage.getCorruptedRecordCount());
        assertEquals("keep this task", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(1).isDone());
        assertFalse(loadedTasks.get(2).isDone());
    }
}
