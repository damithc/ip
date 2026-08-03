package duke;

import java.nio.file.Paths;

import duke.command.Command;
import duke.command.CommandHandler;
import duke.command.CommandType;
import duke.exception.DamienException;
import duke.parser.Parser;
import duke.service.TaskService;
import duke.storage.Storage;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Coordinates Damien's user interface, storage, parser, task list, and
 * command handler.
 *
 * <p>The application object owns the state needed during one run, while each
 * collaborator focuses on one part of the chatbot's work.</p>
 */
public class Damien {
    /** The default relative path used to persist Damien's task list. */
    public static final String DEFAULT_DATA_FILE = "data/duke.txt";

    /** Handles interaction with the user through the selected interface. */
    private final Ui ui;

    /** Loads and saves the task list. */
    private final Storage storage;

    /** Interprets commands entered by the user. */
    private final Parser parser;

    /** Stores the tasks managed during this run. */
    private TaskList tasks;

    /** Performs task changes and persistence for parsed commands. */
    private final TaskService taskService;

    /** Executes commands using Damien's collaborators. */
    private final CommandHandler commandHandler;

    /**
     * Creates Damien using the given file for task persistence.
     *
     * @param filePath the path of the task data file
     */
    public Damien(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Creates Damien using the given file and user interface.
     *
     * @param filePath the path of the task data file
     * @param ui the interface that receives user-facing messages
     */
    public Damien(String filePath, Ui ui) {
        this.ui = ui;
        storage = new Storage(Paths.get(filePath));
        parser = new Parser();
        try {
            tasks = storage.load();
        } catch (DamienException exception) {
            ui.showError(exception);
            tasks = new TaskList();
        }
        taskService = new TaskService(tasks, storage);
        commandHandler = new CommandHandler(taskService, ui);
    }

    /**
     * Starts Damien and processes commands until the user says goodbye.
     */
    public void run() {
        showWelcome();

        while (ui.hasNextLine()) {
            String command = ui.readCommand();
            ui.showLine();

            if (!processCommand(command)) {
                break;
            }

            ui.showLine();
        }
    }

    /** Displays the startup message for the current application state. */
    public void showWelcome() {
        ui.showWelcome(storage.getCorruptedRecordCount());
    }

    /**
     * Processes one command without assuming a particular user interface.
     *
     * @param command the complete command entered by the user
     * @return false when the command was {@code bye}; true otherwise
     */
    public boolean processCommand(String command) {
        try {
            Command parsedCommand = parser.parse(command);
            if (parsedCommand.getType() == CommandType.BYE) {
                ui.showGoodbye();
                return false;
            }
            commandHandler.handle(parsedCommand);
        } catch (DamienException exception) {
            ui.showError(exception);
        }
        return true;
    }

    /**
     * Launches Damien with its default data file.
     *
     * @param args command-line arguments, which are not used by Damien
     */
    public static void main(String[] args) {
        new Damien(DEFAULT_DATA_FILE).run();
    }
}
