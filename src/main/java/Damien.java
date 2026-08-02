import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the Damien command-line todo-list chatbot.
 */
public class Damien {
    /** The separator printed around each chatbot response. */
    private static final String LINE = "____________________________________________________________";

    /** The relative path used to persist Damien's task list. */
    private static final String DATA_FILE = "./data/duke.txt";

    /**
     * Starts Damien and processes commands until the user says goodbye.
     *
     * @param args command-line arguments, which are not used by Damien
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(DATA_FILE);
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (DamienException exception) {
            printError(exception);
            tasks = new ArrayList<>();
        }

        System.out.println(LINE);
        System.out.println("Hello! I'm Damien");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(LINE);

            if (CommandType.fromInput(command) == CommandType.BYE) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }

            try {
                processCommand(command, tasks, storage);
            } catch (DamienException exception) {
                printError(exception);
            }

            System.out.println(LINE);
        }
    }

    /**
     * Parses one command and applies its change to the task list.
     *
     * @param command the command entered by the user
     * @param tasks the collection of tasks being managed
     * @param storage the file storage used to persist changes
     * @throws DamienException if the command is invalid
     */
    private static void processCommand(String command, ArrayList<Task> tasks, Storage storage)
            throws DamienException {
        CommandType commandType = CommandType.fromInput(command);
        if (commandType == null) {
            throw new DamienException("I'm sorry, but I don't know what that means :-(");
        }

        switch (commandType) {
        case LIST:
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
            break;
        case MARK:
            int taskIndex = getTaskIndex(command, commandType.getKeyword());
            if (isValidTaskIndex(taskIndex, tasks.size())) {
                tasks.get(taskIndex).markAsDone();
                storage.save(tasks);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks.get(taskIndex));
            } else {
                throw invalidTaskIndexException(taskIndex);
            }
            break;
        case UNMARK:
            taskIndex = getTaskIndex(command, commandType.getKeyword());
            if (isValidTaskIndex(taskIndex, tasks.size())) {
                tasks.get(taskIndex).unmark();
                storage.save(tasks);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks.get(taskIndex));
            } else {
                throw invalidTaskIndexException(taskIndex);
            }
            break;
        case DELETE:
            taskIndex = getTaskIndex(command, commandType.getKeyword());
            if (isValidTaskIndex(taskIndex, tasks.size())) {
                deleteTask(tasks, taskIndex, storage);
            } else {
                throw invalidTaskIndexException(taskIndex);
            }
            break;
        case TODO:
            String description = command.substring(commandType.getKeyword().length()).trim();
            if (description.isEmpty()) {
                throw new DamienException("The description of a todo cannot be empty.");
            }
            addTask(tasks, new Todo(description), storage);
            break;
        case DEADLINE:
            Task deadline = parseDeadline(command.substring(commandType.getKeyword().length()).trim());
            addTask(tasks, deadline, storage);
            break;
        case EVENT:
            Task event = parseEvent(command.substring(commandType.getKeyword().length()).trim());
            addTask(tasks, event, storage);
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
     */
    private static void deleteTask(ArrayList<Task> tasks, int taskIndex, Storage storage)
            throws DamienException {
        Task deletedTask = tasks.remove(taskIndex);
        storage.save(tasks);

        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + deletedTask);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Converts the one-based task number entered by the user to a zero-based index.
     *
     * @param command the command containing the task number
     * @param commandName the command name used in error messages
     * @return the zero-based task index
     * @throws DamienException if the command does not contain a positive integer
     */
    private static int getTaskIndex(String command, String commandName) throws DamienException {
        String taskNumber = command.substring(commandName.length()).trim();
        if (taskNumber.isEmpty()) {
            throw new DamienException("Please provide a task number after " + commandName
                    + ", for example: " + commandName + " 1.");
        }

        try {
            int parsedTaskNumber = Integer.parseInt(taskNumber);
            if (parsedTaskNumber <= 0) {
                throw new DamienException("Task numbers start at 1. Use list to see valid task numbers.");
            }
            return parsedTaskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new DamienException("The task number after " + commandName
                    + " must be a positive integer, for example: " + commandName + " 1.");
        }
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
     * Checks whether a zero-based task index points to an existing task.
     *
     * @param taskIndex the index to check
     * @param taskCount the current number of tasks
     * @return true when the index is within the task list
     */
    private static boolean isValidTaskIndex(int taskIndex, int taskCount) {
        return taskIndex >= 0 && taskIndex < taskCount;
    }

    /**
     * Parses a deadline command into a deadline task.
     *
     * @param command the part of the command after the word "deadline"
     * @return the parsed deadline
     * @throws DamienException if the description or /by field is missing
     */
    private static Task parseDeadline(String command) throws DamienException {
        int byIndex = command.indexOf("/by");
        if (byIndex < 0) {
            throw new DamienException("A deadline needs a /by field, for example: "
                    + "deadline return book /by Sunday.");
        }

        String description = command.substring(0, byIndex).trim();
        String by = command.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new DamienException("A deadline needs a description before /by, for example: "
                    + "deadline return book /by Sunday.");
        }
        if (by.isEmpty()) {
            throw new DamienException("A deadline needs a date or time after /by, for example: "
                    + "deadline return book /by Sunday.");
        }
        return new Deadline(description, by);
    }

    /**
     * Parses an event command into an event task.
     *
     * @param command the part of the command after the word "event"
     * @return the parsed event
     * @throws DamienException if the description, /from field, or /to field is missing
     */
    private static Task parseEvent(String command) throws DamienException {
        int fromIndex = command.indexOf("/from");
        int toIndex = command.indexOf("/to", fromIndex + "/from".length());
        if (fromIndex < 0) {
            throw new DamienException("An event needs a /from field, for example: "
                    + "event meeting /from 2pm /to 4pm.");
        }
        if (toIndex < 0) {
            throw new DamienException("An event needs a /to field, for example: "
                    + "event meeting /from 2pm /to 4pm.");
        }

        String description = command.substring(0, fromIndex).trim();
        String from = command.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = command.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new DamienException("An event needs a description before /from, for example: "
                    + "event meeting /from 2pm /to 4pm.");
        }
        if (from.isEmpty()) {
            throw new DamienException("An event needs a start time after /from, for example: "
                    + "event meeting /from 2pm /to 4pm.");
        }
        if (to.isEmpty()) {
            throw new DamienException("An event needs an end time after /to, for example: "
                    + "event meeting /from 2pm /to 4pm.");
        }
        return new Event(description, from, to);
    }

    /**
     * Adds a task to the collection and reports the updated list size.
     *
     * @param tasks the collection of tasks being managed
     * @param task the task to add
     * @param storage the file storage used to persist changes
     */
    private static void addTask(ArrayList<Task> tasks, Task task, Storage storage)
            throws DamienException {
        tasks.add(task);
        storage.save(tasks);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Prints a user-friendly error message for a rejected command.
     *
     * @param exception the error raised while processing the command
     */
    private static void printError(DamienException exception) {
        System.out.println(" OOPS!!! " + exception.getMessage());
    }
}
