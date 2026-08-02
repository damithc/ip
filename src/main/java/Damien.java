import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Runs the Damien command-line todo-list chatbot.
 */
public class Damien {
    /** The relative path used to persist Damien's task list. */
    private static final Path DATA_FILE = Paths.get("data", "duke.txt");

    /**
     * Starts Damien and processes commands until the user says goodbye.
     *
     * @param args command-line arguments, which are not used by Damien
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(DATA_FILE);
        Parser parser = new Parser();
        TaskList tasks;
        try {
            tasks = storage.load();
        } catch (DamienException exception) {
            ui.showError(exception);
            tasks = new TaskList();
        }

        ui.showWelcome(storage.getCorruptedRecordCount());

        while (ui.hasNextLine()) {
            String command = ui.readCommand();
            ui.showLine();

            if (CommandType.fromInput(command) == CommandType.BYE) {
                ui.showGoodbye();
                break;
            }

            try {
                processCommand(command, tasks, storage, ui, parser);
            } catch (DamienException exception) {
                ui.showError(exception);
            }

            ui.showLine();
        }
    }

    /**
     * Parses one command and applies its change to the task list.
     *
     * @param command the command entered by the user
     * @param tasks the collection of tasks being managed
     * @param storage the file storage used to persist changes
     * @param ui the user interface used to display responses
     * @param parser the parser used to interpret commands
     * @throws DamienException if the command is invalid
     */
    private static void processCommand(String command, TaskList tasks, Storage storage, Ui ui,
            Parser parser)
            throws DamienException {
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
                deleteTask(tasks, taskIndex, storage, ui);
            } else {
                throw invalidTaskIndexException(taskIndex);
            }
            break;
        case TODO:
            String description = parser.parseTodoDescription(command, commandType);
            addTask(tasks, new Todo(description), storage, ui);
            break;
        case DEADLINE:
            Task deadline = parser.parseDeadline(command, commandType);
            addTask(tasks, deadline, storage, ui);
            break;
        case EVENT:
            Task event = parser.parseEvent(command, commandType);
            addTask(tasks, event, storage, ui);
            break;
        default:
            throw new DamienException("I'm sorry, but I don't know what that means :-(");
        }
    }

    /**
     * Removes the selected task and reports the updated list size.
     *
     * @param tasks the collection of tasks being managed
     * @param taskIndex the zero-based index of the task to remove
     * @param storage the file storage used to persist changes
     * @param ui the user interface used to display responses
     */
    private static void deleteTask(TaskList tasks, int taskIndex, Storage storage, Ui ui)
            throws DamienException {
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
    private static DamienException invalidTaskIndexException(int taskIndex) {
        return new DamienException("Task " + (taskIndex + 1)
                + " does not exist. Use list to see valid task numbers.");
    }

    /**
     * Adds a task to the collection and reports the updated list size.
     *
     * @param tasks the collection of tasks being managed
     * @param task the task to add
     * @param storage the file storage used to persist changes
     * @param ui the user interface used to display responses
     */
    private static void addTask(TaskList tasks, Task task, Storage storage, Ui ui)
            throws DamienException {
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
