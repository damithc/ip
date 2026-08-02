import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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

    /**
     * Creates storage backed by the given file path.
     *
     * @param fileName the path of the task data file
     */
    public Storage(String fileName) {
        this.filePath = Paths.get(fileName);
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
    public ArrayList<Task> load() throws DamienException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException exception) {
            throw new DamienException("Could not load tasks from " + filePath + ".");
        }
        return tasks;
    }

    /**
     * Saves the current tasks to the data file.
     *
     * <p>The parent directory is created automatically when necessary.</p>
     *
     * @param tasks the tasks to save
     * @throws DamienException if the data file cannot be written
     */
    public void save(ArrayList<Task> tasks) throws DamienException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (Task task : tasks) {
                    writer.write(formatTask(task));
                    writer.newLine();
                }
            }
        } catch (IOException exception) {
            throw new DamienException("Could not save tasks to " + filePath + ".");
        }
    }

    /**
     * Converts one saved line into a task.
     *
     * @param line the saved task record
     * @return the parsed task, or null when the record is invalid
     */
    private Task parseTask(String line) {
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3) {
            return null;
        }

        String type = fields[0];
        String status = fields[1];
        String description = fields[2];
        if (description.isEmpty() || !(status.equals("0") || status.equals("1"))) {
            return null;
        }

        Task task;
        switch (type) {
        case "T":
            if (fields.length != 3) {
                return null;
            }
            task = new Todo(description);
            break;
        case "D":
            if (fields.length != 4 || fields[3].isEmpty()) {
                return null;
            }
            task = new Deadline(description, fields[3]);
            break;
        case "E":
            if (fields.length != 5 || fields[3].isEmpty() || fields[4].isEmpty()) {
                return null;
            }
            task = new Event(description, fields[3], fields[4]);
            break;
        default:
            return null;
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Converts a task into its one-line file representation.
     *
     * @param task the task to format
     * @return the saved task record
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D | " + status + " | " + task.getDescription() + " | " + deadline.getBy();
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + status + " | " + task.getDescription() + " | "
                    + event.getFrom() + " | " + event.getTo();
        }
        return "T | " + status + " | " + task.getDescription();
    }
}
