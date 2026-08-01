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
                    printError("Please enter a valid task number to mark.");
                }
            } else if (command.equals(UNMARK_COMMAND) || command.startsWith(UNMARK_PREFIX)) {
                int taskIndex = getTaskIndex(command, UNMARK_COMMAND);
                if (isValidTaskIndex(taskIndex, taskCount)) {
                    tasks[taskIndex].unmark();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks[taskIndex]);
                } else {
                    printError("Please enter a valid task number to unmark.");
                }
            } else if (command.equals(TODO_COMMAND) || command.startsWith(TODO_PREFIX)) {
                String description = command.substring(TODO_COMMAND.length()).trim();
                if (description.isEmpty()) {
                    printError("The description of a todo cannot be empty.");
                } else {
                    taskCount = addTask(tasks, taskCount, new Todo(description));
                }
            } else if (command.equals(DEADLINE_COMMAND) || command.startsWith(DEADLINE_PREFIX)) {
                Task deadline = parseDeadline(command.substring(DEADLINE_COMMAND.length()).trim());
                if (deadline != null) {
                    taskCount = addTask(tasks, taskCount, deadline);
                } else {
                    printError("A deadline needs a description and a /by date or time.");
                }
            } else if (command.equals(EVENT_COMMAND) || command.startsWith(EVENT_PREFIX)) {
                Task event = parseEvent(command.substring(EVENT_COMMAND.length()).trim());
                if (event != null) {
                    taskCount = addTask(tasks, taskCount, event);
                } else {
                    printError("An event needs a description, a /from time, and a /to time.");
                }
            } else {
                printError("I'm sorry, but I don't know what that means :-(");
            }

            System.out.println(LINE);
        }
    }

    private static int getTaskIndex(String command, String commandName) {
        try {
            return Integer.parseInt(command.substring(commandName.length()).trim()) - 1;
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private static boolean isValidTaskIndex(int taskIndex, int taskCount) {
        return taskIndex >= 0 && taskIndex < taskCount;
    }

    private static Task parseDeadline(String command) {
        int byIndex = command.indexOf("/by");
        if (byIndex < 0) {
            return null;
        }

        String description = command.substring(0, byIndex).trim();
        String by = command.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            return null;
        }
        return new Deadline(description, by);
    }

    private static Task parseEvent(String command) {
        int fromIndex = command.indexOf("/from");
        int toIndex = command.indexOf("/to", fromIndex + "/from".length());
        if (fromIndex < 0 || toIndex < 0) {
            return null;
        }

        String description = command.substring(0, fromIndex).trim();
        String from = command.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = command.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            return null;
        }
        return new Event(description, from, to);
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) {
        if (taskCount >= MAX_TASKS) {
            return taskCount;
        }

        tasks[taskCount] = task;
        taskCount++;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        return taskCount;
    }

    private static void printError(String message) {
        System.out.println(" OOPS!!! " + message);
    }
}
