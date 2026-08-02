package duke.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import duke.exception.DamienException;
import duke.task.Task;
import duke.task.TaskList;

/**
 * Loads and saves Damien's task list in a text file.
 *
 * <p>Each task is stored on one line. The first field identifies the task
 * type, the second field stores completion as {@code 1} or {@code 0}, and the
 * remaining fields store the task data. For example, a deadline is stored as
 * {@code D | 0 | return book | Sunday}.</p>
 */
public class Storage {
    /** The file that contains the saved task list. */
    private final Path filePath;

    /** Creates task objects from saved records. */
    private final TaskFactory taskFactory;

    /** The number of malformed records ignored during the most recent load. */
    private int corruptedRecordCount;

    /**
     * Creates storage backed by the given file path.
     *
     * @param filePath the path of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
        taskFactory = new TaskFactory();
    }

    /**
     * Loads all valid tasks from the data file.
     *
     * <p>A missing file means that Damien is being started for the first time,
     * so an empty task list is returned. Invalid records are ignored so that a
     * single malformed line does not make all other saved tasks unavailable.</p>
     *
     * @return the tasks read from the data file
     * @throws DamienException if the data file cannot be read
     */
    public TaskList load() throws DamienException {
        TaskList tasks = new TaskList();
        corruptedRecordCount = 0;
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Task task = taskFactory.createFromStorage(line);
                if (task != null) {
                    tasks.add(task);
                } else {
                    corruptedRecordCount++;
                }
            }
        } catch (IOException exception) {
            throw new DamienException("Could not load tasks from " + filePath + ".");
        }
        return tasks;
    }

    /**
     * Returns how many malformed records were ignored during the most recent load.
     *
     * @return the number of corrupted records skipped during loading
     */
    public int getCorruptedRecordCount() {
        return corruptedRecordCount;
    }

    /**
     * Saves the current tasks to the data file.
     *
     * <p>The parent directory is created automatically when necessary.</p>
     *
     * @param tasks the tasks to save
     * @throws DamienException if the data file cannot be written
     */
    public void save(TaskList tasks) throws DamienException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (int i = 0; i < tasks.size(); i++) {
                    writer.write(tasks.get(i).toStorageString());
                    writer.newLine();
                }
            }
        } catch (IOException exception) {
            throw new DamienException("Could not save tasks to " + filePath + ".");
        }
    }

}
