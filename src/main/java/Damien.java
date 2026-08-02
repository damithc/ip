import java.util.Scanner;

public class Damien {
    private static final String LINE = "____________________________________________________________";
    private static final int MAX_TASKS = 100;
    private static final String TODO_COMMAND = "todo";
    private static final String TODO_PREFIX = "todo ";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String EVENT_COMMAND = "event";
    private static final String EVENT_PREFIX = "event ";
    private static final String MARK_COMMAND = "mark";
    private static final String MARK_PREFIX = "mark ";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String UNMARK_PREFIX = "unmark ";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        System.out.println(LINE);
        System.out.println("Hello! I'm Damien");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(LINE);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }

            try {
                taskCount = processCommand(command, tasks, taskCount);
            } catch (DamienException exception) {
                printError(exception);
            }

            System.out.println(LINE);
        }
    }

    private static int processCommand(String command, Task[] tasks, int taskCount)
            throws DamienException {
        if (command.equals("list")) {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < taskCount; i++) {
                System.out.println((i + 1) + "." + tasks[i]);
            }
        } else if (command.equals(MARK_COMMAND) || command.startsWith(MARK_PREFIX)) {
            int taskIndex = getTaskIndex(command, MARK_COMMAND);
            if (isValidTaskIndex(taskIndex, taskCount)) {
                tasks[taskIndex].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[taskIndex]);
            } else {
                throw invalidTaskIndexException(taskIndex);
            }
        } else if (command.equals(UNMARK_COMMAND) || command.startsWith(UNMARK_PREFIX)) {
            int taskIndex = getTaskIndex(command, UNMARK_COMMAND);
            if (isValidTaskIndex(taskIndex, taskCount)) {
                tasks[taskIndex].unmark();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[taskIndex]);
            } else {
                throw invalidTaskIndexException(taskIndex);
            }
        } else if (command.equals(TODO_COMMAND) || command.startsWith(TODO_PREFIX)) {
            String description = command.substring(TODO_COMMAND.length()).trim();
            if (description.isEmpty()) {
                throw new DamienException("The description of a todo cannot be empty.");
            }
            taskCount = addTask(tasks, taskCount, new Todo(description));
        } else if (command.equals(DEADLINE_COMMAND) || command.startsWith(DEADLINE_PREFIX)) {
            Task deadline = parseDeadline(command.substring(DEADLINE_COMMAND.length()).trim());
            taskCount = addTask(tasks, taskCount, deadline);
        } else if (command.equals(EVENT_COMMAND) || command.startsWith(EVENT_PREFIX)) {
            Task event = parseEvent(command.substring(EVENT_COMMAND.length()).trim());
            taskCount = addTask(tasks, taskCount, event);
        } else {
            throw new DamienException("I'm sorry, but I don't know what that means :-(");
        }

        return taskCount;
    }

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

    private static DamienException invalidTaskIndexException(int taskIndex) {
        return new DamienException("Task " + (taskIndex + 1)
                + " does not exist. Use list to see valid task numbers.");
    }

    private static boolean isValidTaskIndex(int taskIndex, int taskCount) {
        return taskIndex >= 0 && taskIndex < taskCount;
    }

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

    private static int addTask(Task[] tasks, int taskCount, Task task) throws DamienException {
        if (taskCount >= MAX_TASKS) {
            throw new DamienException("The task list is full; it can contain at most "
                    + MAX_TASKS + " tasks.");
        }

        tasks[taskCount] = task;
        taskCount++;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        return taskCount;
    }

    private static void printError(DamienException exception) {
        System.out.println(" OOPS!!! " + exception.getMessage());
    }
}
