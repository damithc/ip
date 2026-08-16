package duke.task;

import java.util.Optional;

/**
 * Represents a task with a description and a completion status.
 */
public class Task {
    /** The text describing the work to be completed. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /** The optional fixed amount of time this task requires. */
    private final Duration duration;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the work to be completed
     */
    public Task(String description) {
        this(description, null);
    }

    /**
     * Creates an incomplete task with an optional fixed duration.
     *
     * @param description the text describing the work to be completed
     * @param duration the task's fixed duration, or {@code null} when unspecified
     */
    protected Task(String description, Duration duration) {
        assert description != null && !description.isBlank()
                : "A task must have a non-blank description.";
        this.description = description;
        this.isDone = false;
        this.duration = duration;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns the task description for persistence and other task operations.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true when the task is complete
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the fixed duration when one was supplied for this task.
     *
     * @return the optional task duration
     */
    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    /**
     * Returns the optional duration portion of a saved task record.
     *
     * @return an empty string, or a storage field containing whole minutes
     */
    protected String getDurationStorageSuffix() {
        return duration == null ? "" : " | " + duration.getMinutes();
    }

    /**
     * Returns the optional duration badge used in task-list displays.
     *
     * @return an empty string, or the fixed-duration display badge
     */
    protected String getDurationDisplaySuffix() {
        return duration == null ? "" : " >> " + duration + " <<";
    }

    /**
     * Returns this task in the one-line format used by persistent storage.
     *
     * @return the task's saved representation
     */
    public String toStorageString() {
        String status = isDone ? "1" : "0";
        return "T | " + status + " | " + description;
    }

    /**
     * Returns the task's completion marker and description.
     *
     * @return the task formatted for display in the task list
     */
    @Override
    public String toString() {
        String status = isDone ? "[X]" : "[ ]";
        return status + " " + description;
    }
}
