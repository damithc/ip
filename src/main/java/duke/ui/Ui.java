package duke.ui;

import java.io.PrintStream;
import java.util.Scanner;

import duke.exception.DamienException;
import duke.task.Task;
import duke.task.TaskList;

/**
 * Handles Damien's interaction with the user through the command line.
 *
 * <p>This class keeps input reading and response formatting out of the main
 * application logic so those responsibilities can evolve independently.</p>
 */
public class Ui {
    /** The separator printed around each chatbot response. */
    private static final String LINE = "____________________________________________________________";

    /** Reads commands entered by the user. */
    private final Scanner scanner;

    /** Receives formatted output for the selected user interface. */
    private final PrintStream output;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        this(System.out);
    }

    /**
     * Creates a user interface that writes to the given output stream.
     *
     * @param output the stream that receives user-facing messages
     */
    public Ui(PrintStream output) {
        scanner = new Scanner(System.in);
        this.output = output;
    }

    /**
     * Prints one line of user-facing output.
     *
     * @param line the line to print
     */
    protected void printLine(String line) {
        output.println(line);
    }

    /**
     * Checks whether another command is available from the user.
     *
     * @return true when another input line can be read
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the next command line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Damien's startup message and any warning about saved records.
     *
     * @param corruptedRecordCount the number of invalid saved records skipped
     */
    public void showWelcome(int corruptedRecordCount) {
        showLine();
        printLine("Hello! I'm Damien");
        printLine("What can I do for you?");
        if (corruptedRecordCount > 0) {
            showCorruptionWarning(corruptedRecordCount);
        }
        showLine();
    }

    /** Prints the separator used around a chatbot response. */
    public void showLine() {
        printLine(LINE);
    }

    /** Displays Damien's goodbye message. */
    public void showGoodbye() {
        printLine("Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Displays a user-facing error message.
     *
     * @param exception the error to explain to the user
     */
    public void showError(DamienException exception) {
        printLine(" OOPS!!! " + exception.getMessage());
    }

    /**
     * Displays the current tasks in their numbered order.
     *
     * @param tasks the tasks to display
     */
    public void showTaskList(TaskList tasks) {
        showTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Displays tasks matching a search keyword in their matching order.
     *
     * @param tasks the matching tasks to display
     */
    public void showMatchingTasks(TaskList tasks) {
        showTasks("Here are the matching tasks in your list:", tasks);
    }

    /**
     * Displays a heading followed by tasks in their numbered order.
     *
     * @param heading the heading to display before the tasks
     * @param tasks the tasks to display
     */
    private void showTasks(String heading, TaskList tasks) {
        printLine(heading);
        for (int i = 0; i < tasks.size(); i++) {
            printLine((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays the result of adding a task.
     *
     * @param task the task that was added
     * @param taskCount the updated number of tasks
     */
    public void showTaskAdded(Task task, int taskCount) {
        printLine("Got it. I've added this task:");
        printLine("  " + task);
        printLine("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the result of marking a task as done.
     *
     * @param task the task that was marked as done
     */
    public void showTaskMarkedAsDone(Task task) {
        printLine("Nice! I've marked this task as done:");
        printLine("  " + task);
    }

    /**
     * Displays the result of marking a task as not done.
     *
     * @param task the task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        printLine("OK, I've marked this task as not done yet:");
        printLine("  " + task);
    }

    /**
     * Displays the result of deleting a task.
     *
     * @param task the task that was deleted
     * @param taskCount the updated number of tasks
     */
    public void showTaskDeleted(Task task, int taskCount) {
        printLine("Noted. I've removed this task:");
        printLine("  " + task);
        printLine("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Tells the user that invalid saved records were skipped during startup.
     *
     * @param corruptedRecordCount the number of records that were skipped
     */
    private void showCorruptionWarning(int corruptedRecordCount) {
        String recordLabel = corruptedRecordCount == 1 ? "record" : "records";
        String pronoun = corruptedRecordCount == 1 ? "it" : "them";
        printLine("Warning: I found " + corruptedRecordCount
                + " invalid saved task " + recordLabel + " and skipped " + pronoun + ".");
    }
}
