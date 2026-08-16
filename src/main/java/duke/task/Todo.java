package duke.task;

/**
 * A task without a deadline or scheduled time interval.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete ToDo task.
     *
     * @param description the work to be completed
     */
    public Todo(String description) {
        this(description, null);
    }

    /**
     * Creates an incomplete ToDo task with an optional fixed duration.
     *
     * @param description the work to be completed
     * @param duration the fixed time required, or {@code null} when unspecified
     */
    public Todo(String description, Duration duration) {
        super(description, duration);
    }

    @Override
    public String toStorageString() {
        String status = isDone() ? "1" : "0";
        return "T | " + status + " | " + getDescription() + getDurationStorageSuffix();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString() + getDurationDisplaySuffix();
    }
}
