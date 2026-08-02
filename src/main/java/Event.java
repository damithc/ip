/**
 * A task that takes place during a specified time interval.
 */
public class Event extends Task {
    /** The date or time when this event starts. */
    protected String from;

    /** The date or time when this event ends. */
    protected String to;

    /**
     * Creates an event task.
     *
     * @param description the event to attend or organize
     * @param from the date or time when the event starts
     * @param to the date or time when the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
