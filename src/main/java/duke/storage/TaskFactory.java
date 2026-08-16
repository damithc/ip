package duke.storage;

import duke.task.Deadline;
import duke.task.Duration;
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
    /** Record marker used for ToDo tasks. */
    private static final String TODO_RECORD_TYPE = "T";

    /** Record marker used for deadline tasks. */
    private static final String DEADLINE_RECORD_TYPE = "D";

    /** Record marker used for event tasks. */
    private static final String EVENT_RECORD_TYPE = "E";

    /** Status marker used for incomplete tasks. */
    private static final String INCOMPLETE_STATUS = "0";

    /** Status marker used for completed tasks. */
    private static final String COMPLETED_STATUS = "1";

    /**
     * Creates a task from one saved record.
     *
     * @param line the saved task record
     * @return the parsed task, or {@code null} when the record is invalid
     */
    public Task createFromStorage(String line) {
        assert line != null : "A stored task record must not be null.";
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3) {
            return null;
        }

        String type = fields[0];
        String status = fields[1];
        String description = fields[2];
        if (description.isEmpty() || !isValidStatus(status)) {
            return null;
        }

        Task task;
        switch (type) {
            case TODO_RECORD_TYPE:
                if (fields.length != 3 && fields.length != 4) {
                    return null;
                }
                try {
                    task = new Todo(description, parseDuration(fields, 3));
                } catch (IllegalArgumentException exception) {
                    return null;
                }
                break;
            case DEADLINE_RECORD_TYPE:
                if ((fields.length != 4 && fields.length != 5) || fields[3].isEmpty()) {
                    return null;
                }
                try {
                    task = new Deadline(description, fields[3], parseDuration(fields, 4));
                } catch (IllegalArgumentException exception) {
                    return null;
                }
                break;
            case EVENT_RECORD_TYPE:
                if (fields.length != 5 || fields[3].isEmpty() || fields[4].isEmpty()) {
                    return null;
                }
                task = new Event(description, fields[3], fields[4]);
                break;
            default:
                return null;
        }

        boolean shouldBeDone = COMPLETED_STATUS.equals(status);
        if (shouldBeDone) {
            task.markAsDone();
        }
        assert task.isDone() == shouldBeDone : "A task must preserve its stored completion status.";
        return task;
    }

    /**
     * Reads an optional positive whole-minute duration field from a stored record.
     *
     * @param fields the split storage record
     * @param durationIndex the index where a duration is present when supplied
     * @return the parsed duration, or {@code null} when the record has no duration field
     * @throws IllegalArgumentException if the duration field is absent, empty, or invalid
     */
    private Duration parseDuration(String[] fields, int durationIndex) {
        if (fields.length == durationIndex) {
            return null;
        }
        if (fields[durationIndex].isEmpty()) {
            throw new IllegalArgumentException("A stored duration must not be empty.");
        }
        try {
            return new Duration(Integer.parseInt(fields[durationIndex]));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("A stored duration must be a whole number.", exception);
        }
    }

    /**
     * Checks whether a saved status marker is supported by the storage format.
     *
     * @param status the saved completion marker
     * @return true when the marker represents an incomplete or completed task
     */
    private static boolean isValidStatus(String status) {
        return INCOMPLETE_STATUS.equals(status) || COMPLETED_STATUS.equals(status);
    }
}
