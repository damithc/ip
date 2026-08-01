import java.util.Scanner;

public class Duke {
    private static final String LINE = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[MAX_TASKS];
        int taskCount = 0;

        System.out.println(LINE);
        System.out.println("Hello! I'm Duke");
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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
            }

            System.out.println(LINE);
        }
    }
}
