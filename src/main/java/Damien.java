import java.nio.file.Paths;

/**
 * Coordinates Damien's user interface, storage, parser, task list, and
 * command handler.
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

    /** Executes commands using Damien's collaborators. */
    private final CommandHandler commandHandler;

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
        commandHandler = new CommandHandler(tasks, storage, ui, parser);
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
                commandHandler.handle(command);
            } catch (DamienException exception) {
                ui.showError(exception);
            }

            ui.showLine();
        }
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
