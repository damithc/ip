/**
 * Executes commands after they have been entered by the user.
 *
 * <p>This class coordinates parsing, task-list changes, persistence, and
 * responses for one running Damien application.</p>
 */
public class CommandHandler {
    /** The task list changed by commands. */
    private final TaskList tasks;

    /** Saves task-list changes to the data file. */
    private final Storage storage;

    /** Displays command results and errors to the user. */
    private final Ui ui;

    /** Interprets command text and extracts command arguments. */
    private final Parser parser;

    /**
     * Creates a command handler using the given application collaborators.
     *
     * @param tasks the task list to change
     * @param storage the storage used to save changes
     * @param ui the user interface used to show results
     * @param parser the parser used to interpret commands
     */
    public CommandHandler(TaskList tasks, Storage storage, Ui ui, Parser parser) {
        this.tasks = tasks;
        this.storage = storage;
        this.ui = ui;
        this.parser = parser;
    }

    /**
     * Parses one command and applies its change to the task list.
     *
     * @param command the command entered by the user
     * @throws DamienException if the command is invalid
     */
    public void handle(String command) throws DamienException {
        CommandType commandType = parser.parseCommand(command);

        switch (commandType) {
        case LIST:
            ui.showTaskList(tasks);
            break;
        case MARK:
            int taskIndex = parser.parseTaskIndex(command, commandType);
            if (tasks.isValidIndex(taskIndex)) {
                tasks.markAsDone(taskIndex);
                storage.save(tasks);
                ui.showTaskMarkedAsDone(tasks.get(taskIndex));
            } else {
                throw invalidTaskIndexException(taskIndex);
            }
            break;
        case UNMARK:
            taskIndex = parser.parseTaskIndex(command, commandType);
            if (tasks.isValidIndex(taskIndex)) {
                tasks.unmark(taskIndex);
                storage.save(tasks);
                ui.showTaskUnmarked(tasks.get(taskIndex));
            } else {
                throw invalidTaskIndexException(taskIndex);
            }
            break;
        case DELETE:
            taskIndex = parser.parseTaskIndex(command, commandType);
            if (tasks.isValidIndex(taskIndex)) {
                deleteTask(taskIndex);
            } else {
                throw invalidTaskIndexException(taskIndex);
            }
            break;
        case TODO:
            String description = parser.parseTodoDescription(command, commandType);
            addTask(new Todo(description));
            break;
        case DEADLINE:
            Task deadline = parser.parseDeadline(command, commandType);
            addTask(deadline);
            break;
        case EVENT:
            Task event = parser.parseEvent(command, commandType);
            addTask(event);
            break;
        default:
            throw new DamienException("I'm sorry, but I don't know what that means :-(");
        }
    }

    /**
     * Removes the selected task and reports the updated list size.
     *
     * @param taskIndex the zero-based index of the task to remove
     */
    private void deleteTask(int taskIndex) throws DamienException {
        Task deletedTask = tasks.remove(taskIndex);
        storage.save(tasks);

        ui.showTaskDeleted(deletedTask, tasks.size());
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

    /**
     * Adds a task to the list and reports the updated list size.
     *
     * @param task the task to add
     */
    private void addTask(Task task) throws DamienException {
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
