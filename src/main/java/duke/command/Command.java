package duke.command;

import duke.task.Task;

/**
 * Represents a user command after it has been interpreted by the parser.
 *
 * <p>A command can carry either a task index or a newly created task,
 * depending on the command type. A {@code find} command carries a search
 * keyword. Commands such as {@code list} and {@code bye} carry none of these.</p>
 */
public class Command {
    /** The kind of action requested by the user. */
    private final CommandType type;

    /** The zero-based task index for mark, unmark, and delete commands. */
    private final Integer taskIndex;

    /** The task created by a todo, deadline, or event command. */
    private final Task task;

    /** The keyword used by a find command. */
    private final String keyword;

    /**
     * Creates a command that does not need additional data.
     *
     * @param type the kind of command
     */
    public Command(CommandType type) {
        this(type, null, null, null);
    }

    /**
     * Creates a command that refers to an existing task.
     *
     * @param type the kind of command
     * @param taskIndex the zero-based index of the task
     */
    public Command(CommandType type, int taskIndex) {
        this(type, taskIndex, null, null);
    }

    /**
     * Creates a command that adds a newly parsed task.
     *
     * @param type the kind of command
     * @param task the task to add
     */
    public Command(CommandType type, Task task) {
        this(type, null, task, null);
    }

    /**
     * Creates a command that searches task descriptions for a keyword.
     *
     * @param type the kind of command
     * @param keyword the keyword to search for
     */
    public Command(CommandType type, String keyword) {
        this(type, null, null, keyword);
    }

    /**
     * Creates a command with its optional parsed data.
     *
     * @param type the kind of command
     * @param taskIndex the referenced task index, if any
     * @param task the new task, if any
     * @param keyword the search keyword, if any
     */
    private Command(CommandType type, Integer taskIndex, Task task, String keyword) {
        this.type = type;
        this.taskIndex = taskIndex;
        this.task = task;
        this.keyword = keyword;
    }

    /**
     * Returns the kind of command.
     *
     * @return the command type
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Returns the referenced task index.
     *
     * @return the zero-based task index, or {@code null} when not applicable
     */
    public Integer getTaskIndex() {
        return taskIndex;
    }

    /**
     * Returns the new task carried by this command.
     *
     * @return the task to add, or {@code null} when not applicable
     */
    public Task getTask() {
        return task;
    }

    /**
     * Returns the search keyword carried by this command.
     *
     * @return the search keyword, or {@code null} when not applicable
     */
    public String getKeyword() {
        return keyword;
    }
}
