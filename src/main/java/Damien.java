import java.util.Scanner;

public class Damien {
    private static final String LINE = "____________________________________________________________";
    private static final int MAX_TASKS = 100;
    private static final String TODO_PREFIX = "todo ";
    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String EVENT_PREFIX = "event ";

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
            } else if (command.startsWith("mark ")) {
                int taskIndex = getTaskIndex(command, "mark ");
                if (isValidTaskIndex(taskIndex, taskCount)) {
                    tasks[taskIndex].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks[taskIndex]);
                }
            } else if (command.startsWith("unmark ")) {
                int taskIndex = getTaskIndex(command, "unmark ");
                if (isValidTaskIndex(taskIndex, taskCount)) {
                    tasks[taskIndex].unmark();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks[taskIndex]);
                }
            } else if (command.startsWith(TODO_PREFIX)) {
                taskCount = addTask(tasks, taskCount,
                        new Task(command.substring(TODO_PREFIX.length()).trim()));
            } else if (command.startsWith(DEADLINE_PREFIX)) {
                Task deadline = parseDeadline(command.substring(DEADLINE_PREFIX.length()).trim());
                if (deadline != null) {
                    taskCount = addTask(tasks, taskCount, deadline);
                }
            } else if (command.startsWith(EVENT_PREFIX)) {
                Task event = parseEvent(command.substring(EVENT_PREFIX.length()).trim());
                if (event != null) {
                    taskCount = addTask(tasks, taskCount, event);
                }
            } else {
                // Keep accepting plain text as a ToDo, as in the previous increment.
                taskCount = addTask(tasks, taskCount, new Task(command));
            }

            System.out.println(LINE);
        }
    }

    private static int getTaskIndex(String command, String commandPrefix) {
        try {
            return Integer.parseInt(command.substring(commandPrefix.length())) - 1;
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
        return new Task(description, by);
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
        return new Task(description, from, to);
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
}
