package duke.parser;

import duke.command.Command;
import duke.command.CommandType;
import duke.exception.DamienException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.Todo;

/**
 * Interprets user commands and extracts the information needed to execute them.
 */
public class Parser {
    /**
     * Interprets a complete input line into a command ready for execution.
     *
     * @param input the complete line entered by the user
     * @return the parsed command
     * @throws DamienException if the input is not a recognised command
     */
    public Command parse(String input) throws DamienException {
        CommandType commandType = CommandType.fromInput(input);
        if (commandType == null) {
            throw new DamienException("I'm sorry, but I don't know what that means :-(");
        }

        switch (commandType) {
            case MARK:
            // Fallthrough
            case UNMARK:
            // Fallthrough
            case DELETE:
                return new Command(commandType, parseTaskIndex(input, commandType));
            case FIND:
                return new Command(commandType, parseFindKeyword(input, commandType));
            case TODO:
                return new Command(commandType,
                        new Todo(parseTodoDescription(input, commandType)));
            case DEADLINE:
                return new Command(commandType, parseDeadline(input, commandType));
            case EVENT:
                return new Command(commandType, parseEvent(input, commandType));
            default:
                return new Command(commandType);
        }
    }

    /**
     * Extracts and validates the description of a ToDo command.
     *
     * @param command the complete ToDo command
     * @param commandType the command type identified from the input
     * @return the non-empty ToDo description
     * @throws DamienException if the description is empty
     */
    private String parseTodoDescription(String command, CommandType commandType)
            throws DamienException {
        String description = getArgument(command, commandType);
        if (description.isEmpty()) {
            throw new DamienException("The description of a todo cannot be empty.");
        }
        return description;
    }

    /**
     * Extracts and validates the keyword of a find command.
     *
     * @param command the complete find command
     * @param commandType the command type identified from the input
     * @return the non-empty search keyword
     * @throws DamienException if the keyword is empty
     */
    private String parseFindKeyword(String command, CommandType commandType)
            throws DamienException {
        String keyword = getArgument(command, commandType);
        if (keyword.isEmpty()) {
            throw new DamienException("Please provide a keyword after find, for example: find book.");
        }
        return keyword;
    }

    /**
     * Converts a task number in a command into a zero-based task index.
     *
     * @param command the command containing the task number
     * @param commandType the command type identified from the input
     * @return the zero-based task index
     * @throws DamienException if the command does not contain a positive integer
     */
    private int parseTaskIndex(String command, CommandType commandType) throws DamienException {
        String commandName = commandType.getKeyword();
        String taskNumber = getArgument(command, commandType);
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
     * Parses the part of a deadline command after the command keyword.
     *
     * @param command the complete deadline command
     * @param commandType the command type identified from the input
     * @return the parsed deadline task
     * @throws DamienException if the description or /by field is missing
     */
    private Task parseDeadline(String command, CommandType commandType) throws DamienException {
        command = getArgument(command, commandType);
        int byIndex = command.indexOf("/by");
        if (byIndex < 0) {
            throw new DamienException("A deadline needs a /by field, for example: "
                    + "deadline return book /by 2019-10-15.");
        }

        String description = command.substring(0, byIndex).trim();
        String by = command.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new DamienException("A deadline needs a description before /by, for example: "
                    + "deadline return book /by 2019-10-15.");
        }
        if (by.isEmpty()) {
            throw new DamienException("A deadline needs a date or time after /by, for example: "
                    + "deadline return book /by 2019-10-15.");
        }
        try {
            return new Deadline(description, by);
        } catch (IllegalArgumentException exception) {
            throw new DamienException("A deadline date must use yyyy-MM-dd, optionally followed by HHmm, "
                    + "or d/M/yyyy HHmm, for example: deadline return book /by 2019-10-15.");
        }
    }

    /**
     * Parses the part of an event command after the command keyword.
     *
     * @param command the complete event command
     * @param commandType the command type identified from the input
     * @return the parsed event task
     * @throws DamienException if the description, /from field, or /to field is missing
     */
    private Task parseEvent(String command, CommandType commandType) throws DamienException {
        command = getArgument(command, commandType);
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
     * Extracts the text after a command keyword.
     *
     * @param command the complete command
     * @param commandType the command type identified from the input
     * @return the trimmed command argument
     */
    private String getArgument(String command, CommandType commandType) {
        return command.substring(commandType.getKeyword().length()).trim();
    }
}
