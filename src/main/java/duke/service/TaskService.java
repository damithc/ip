package duke.service;

import duke.exception.DamienException;
import duke.storage.Storage;
import duke.task.Task;
import duke.task.TaskList;

/**
 * Manages task changes and keeps them persisted.
 *
 * <p>This class provides the application-level task operations used by the
 * command handler. {@link TaskList} owns the collection, while this class
 * coordinates validation and saving after a change.</p>
 */
public class TaskService {
    /** The task list changed by this service. */
    private final TaskList tasks;

    /** Saves the task list after a successful change. */
    private final Storage storage;

    /**
     * Creates a task service with the given list and storage.
     *
     * @param tasks the task list to manage
     * @param storage the storage used to persist changes
     */
    public TaskService(TaskList tasks, Storage storage) {
        this.tasks = tasks;
        this.storage = storage;
    }

    /**
     * Returns the task list for read-only display operations.
     *
     * @return the managed task list
     */
    public TaskList getTasks() {
        return tasks;
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task and saves the updated list.
     *
     * @param task the task to add
     * @throws DamienException if the updated list cannot be saved
     */
    public void add(Task task) throws DamienException {
        tasks.add(task);
        storage.save(tasks);
    }

    /**
     * Marks a task as done and saves the updated list.
     *
     * @param taskIndex the zero-based index of the task
     * @return the task that was marked as done
     * @throws DamienException if the index is invalid or the list cannot be saved
     */
    public Task markAsDone(int taskIndex) throws DamienException {
        Task task = getTask(taskIndex);
        tasks.markAsDone(taskIndex);
        storage.save(tasks);
        return task;
    }

    /**
     * Marks a task as not done and saves the updated list.
     *
     * @param taskIndex the zero-based index of the task
     * @return the task that was unmarked
     * @throws DamienException if the index is invalid or the list cannot be saved
     */
    public Task unmark(int taskIndex) throws DamienException {
        Task task = getTask(taskIndex);
        tasks.unmark(taskIndex);
        storage.save(tasks);
        return task;
    }

    /**
     * Deletes a task and saves the updated list.
     *
     * @param taskIndex the zero-based index of the task
     * @return the task that was deleted
     * @throws DamienException if the index is invalid or the list cannot be saved
     */
    public Task delete(int taskIndex) throws DamienException {
        getTask(taskIndex);
        Task deletedTask = tasks.remove(taskIndex);
        storage.save(tasks);
        return deletedTask;
    }

    /**
     * Returns a task after checking that its index exists.
     *
     * @param taskIndex the zero-based index to check
     * @return the task at the given index
     * @throws DamienException if the index is invalid
     */
    private Task getTask(int taskIndex) throws DamienException {
        if (!tasks.isValidIndex(taskIndex)) {
            throw invalidTaskIndexException(taskIndex);
        }
        return tasks.get(taskIndex);
    }

    /**
     * Creates the error used when a task number is outside the current list.
     *
     * @param taskIndex the invalid zero-based task index
     * @return an exception describing the invalid task number
     */
    private DamienException invalidTaskIndexException(int taskIndex) {
        return new DamienException("Task " + (taskIndex + 1)
                + " does not exist. Use list to see valid task numbers.");
    }
}
