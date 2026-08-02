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
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
