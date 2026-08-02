import java.nio.file.Paths;

/**
 * Coordinates Damien's user interface, storage, parser, and task list.
 *
 * <p>The application object owns the state needed during one run, while each
 * collaborator focuses on one part of the chatbot's work.</p>
 */
public class Damien {
    /** The relative path used to persist Damien's task list. */
    private static final String DATA_FILE = "data/duke.txt";

    /** Handles interaction with the user through the command line. */
    private final Ui ui;

    /** Loads and saves the task list. */
    private final Storage storage;

    /** Interprets commands entered by the user. */
    private final Parser parser;

    /** Stores the tasks managed during this run. */
    private TaskList tasks;

    /**
     * Creates Damien using the given file for task persistence.
     *
     * @param filePath the path of the task data file
     */
    public Damien(String filePath) {
        ui = new Ui();
        storage = new Storage(Paths.get(filePath));
        parser = new Parser();
        try {
            tasks = storage.load();
        } catch (DamienException exception) {
            ui.showError(exception);
            tasks = new TaskList();
        }
    }

    /**
     * Starts Damien and processes commands until the user says goodbye.
     */
    public void run() {
        ui.showWelcome(storage.getCorruptedRecordCount());

        while (ui.hasNextLine()) {
            String command = ui.readCommand();
            ui.showLine();

            if (CommandType.fromInput(command) == CommandType.BYE) {
                ui.showGoodbye();
                break;
            }

            try {
                processCommand(command);
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
     * @throws DamienException if the command is invalid
     */
    private void processCommand(String command) throws DamienException {
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

    /**
     * Launches Damien with its default data file.
     *
     * @param args command-line arguments, which are not used by Damien
     */
    public static void main(String[] args) {
        new Damien(DATA_FILE).run();
    }
}
