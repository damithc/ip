import java.util.Scanner;

public class Damien {
    private static final String LINE = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] taskDescriptions = new String[MAX_TASKS];
        boolean[] taskDone = new boolean[MAX_TASKS];
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
                    System.out.println((i + 1) + "." + formatTask(taskDescriptions[i], taskDone[i]));
                }
            } else if (command.startsWith("mark ")) {
                int taskIndex = getTaskIndex(command, "mark ");
                if (isValidTaskIndex(taskIndex, taskCount)) {
                    taskDone[taskIndex] = true;
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + formatTask(taskDescriptions[taskIndex], taskDone[taskIndex]));
                }
            } else if (command.startsWith("unmark ")) {
                int taskIndex = getTaskIndex(command, "unmark ");
                if (isValidTaskIndex(taskIndex, taskCount)) {
                    taskDone[taskIndex] = false;
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + formatTask(taskDescriptions[taskIndex], taskDone[taskIndex]));
                }
            } else {
                taskDescriptions[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
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

    private static String formatTask(String description, boolean isDone) {
        String status = isDone ? "[X]" : "[ ]";
        return status + " " + description;
    }
}
