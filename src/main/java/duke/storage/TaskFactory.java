package duke.storage;

import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.Todo;

/**
 * Creates tasks from their saved text representation.
 *
 * <p>The factory keeps the record format and task subtype construction out of
 * {@link Storage}, allowing storage to focus on reading and writing files.</p>
 */
public class TaskFactory {
    /**
     * Creates a task from one saved record.
     *
     * @param line the saved task record
     * @return the parsed task, or {@code null} when the record is invalid
     */
    public Task createFromStorage(String line) {
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
            try {
                task = new Deadline(description, fields[3]);
            } catch (IllegalArgumentException exception) {
                return null;
            }
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
}
