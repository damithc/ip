package duke.command;

import duke.exception.DamienException;
import duke.service.TaskService;
import duke.task.Task;
import duke.ui.Ui;

/**
 * Executes non-termination commands after they have been entered by the user.
 *
 * <p>This class routes parsed commands and coordinates user-facing responses
 * for one running Damien application.</p>
 */
public class CommandHandler {
    /** Performs task changes and saves the updated list. */
    private final TaskService taskService;

    /** Displays command results and errors to the user. */
    private final Ui ui;

    /**
     * Creates a command handler using the given task service and interface.
     *
     * @param taskService the service used to change and persist tasks
     * @param ui the user interface used to show results
     */
    public CommandHandler(TaskService taskService, Ui ui) {
        this.taskService = taskService;
        this.ui = ui;
    }

    /**
     * Executes one parsed command on the task list.
     *
     * @param command the command interpreted by the parser
     * @throws DamienException if the command is invalid
     */
    public void handle(Command command) throws DamienException {
        assert command != null : "The command handler must receive a parsed command.";
        switch (command.getType()) {
            case LIST:
                ui.showTaskList(taskService.getTasks());
                break;
            case MARK:
                ui.showTaskMarkedAsDone(taskService.markAsDone(command.getTaskIndex()));
                break;
            case UNMARK:
                ui.showTaskUnmarked(taskService.unmark(command.getTaskIndex()));
                break;
            case DELETE:
                deleteTask(command.getTaskIndex());
                break;
            case FIND:
                ui.showMatchingTasks(taskService.find(command.getKeyword()));
                break;
            case TODO:
            // Fallthrough
            case DEADLINE:
            // Fallthrough
            case EVENT:
                addTask(command.getTask());
                break;
            default:
                throw new DamienException("I'm sorry, but I don't know what that means :-(");
        }
    }

    /**
     * Removes the selected task and reports the updated list size.
     *
     * @param taskIndex the zero-based index of the task to remove
     */
    private void deleteTask(int taskIndex) throws DamienException {
        Task deletedTask = taskService.delete(taskIndex);
        ui.showTaskDeleted(deletedTask, taskService.size());
    }

    /**
     * Adds a task to the list and reports the updated list size.
     *
     * @param task the task to add
     */
    private void addTask(Task task) throws DamienException {
        taskService.add(task);
        ui.showTaskAdded(task, taskService.size());
    }
}
