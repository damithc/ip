package duke.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns Damien's tasks and provides operations for changing the collection.
 *
 * <p>Keeping the collection private means that other classes do not need to
 * know whether tasks are stored in an {@link ArrayList} or another structure.</p>
 */
public class TaskList {
    /** The tasks currently managed by Damien. */
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at the given index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether a zero-based index refers to an existing task.
     *
     * @param index the index to check
     * @return true when the index is valid
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Marks the task at a zero-based index as done.
     *
     * @param index the zero-based task index
     */
    public void markAsDone(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at a zero-based index as not done.
     *
     * @param index the zero-based task index
     */
    public void unmark(int index) {
        tasks.get(index).unmark();
    }
}
