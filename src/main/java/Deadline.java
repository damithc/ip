/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The date or time by which this task should be completed. */
    private String by;

    /**
     * Creates a deadline task.
     *
     * @param description the work to be completed
     * @param by the date or time by which the work should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the date or time by which this task should be completed.
     *
     * @return the deadline date or time
     */
    public String getBy() {
        return by;
    }

    @Override
    public String toStorageString() {
        String status = isDone() ? "1" : "0";
        return "D | " + status + " | " + getDescription() + " | " + by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
