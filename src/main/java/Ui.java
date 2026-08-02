import java.util.Scanner;

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

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
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
        System.out.println("Hello! I'm Damien");
        System.out.println("What can I do for you?");
        if (corruptedRecordCount > 0) {
            showCorruptionWarning(corruptedRecordCount);
        }
        showLine();
    }

    /** Prints the separator used around a chatbot response. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Displays Damien's goodbye message. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Displays a user-facing error message.
     *
     * @param exception the error to explain to the user
     */
    public void showError(DamienException exception) {
        System.out.println(" OOPS!!! " + exception.getMessage());
    }

    /**
     * Displays the current tasks in their numbered order.
     *
     * @param tasks the tasks to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays the result of adding a task.
     *
     * @param task the task that was added
     * @param taskCount the updated number of tasks
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the result of marking a task as done.
     *
     * @param task the task that was marked as done
     */
    public void showTaskMarkedAsDone(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Displays the result of marking a task as not done.
     *
     * @param task the task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Displays the result of deleting a task.
     *
     * @param task the task that was deleted
     * @param taskCount the updated number of tasks
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Tells the user that invalid saved records were skipped during startup.
     *
     * @param corruptedRecordCount the number of records that were skipped
     */
    private void showCorruptionWarning(int corruptedRecordCount) {
        String recordLabel = corruptedRecordCount == 1 ? "record" : "records";
        String pronoun = corruptedRecordCount == 1 ? "it" : "them";
        System.out.println("Warning: I found " + corruptedRecordCount
                + " invalid saved task " + recordLabel + " and skipped " + pronoun + ".");
    }
}
