package duke.parser;

import duke.command.Command;
import duke.command.CommandType;
import duke.exception.DamienException;
import duke.task.Deadline;
import duke.task.Duration;
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
        assert input != null : "The parser must receive a command string.";
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
                return new Command(commandType, parseTodo(input, commandType));
            case DEADLINE:
                return new Command(commandType, parseDeadline(input, commandType));
            case EVENT:
                return new Command(commandType, parseEvent(input, commandType));
            default:
                assert commandType == CommandType.BYE || commandType == CommandType.LIST
                        : "Only commands without arguments should reach the default case.";
                return new Command(commandType);
        }
    }

    /**
     * Extracts and validates a ToDo description and its optional duration.
     *
     * @param command the complete ToDo command
     * @param commandType the command type identified from the input
     * @return the parsed ToDo task
     * @throws DamienException if the description or supplied duration is invalid
     */
    private Task parseTodo(String command, CommandType commandType)
            throws DamienException {
        TaskDetails details = parseTaskDetails(getArgument(command, commandType));
        if (details.description.isEmpty()) {
            throw new DamienException("The description of a todo cannot be empty.");
        }
        return new Todo(details.description, parseDuration(details.duration, "todo"));
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
        String argument = getArgument(command, commandType);
        int byIndex = argument.indexOf("/by");
        int durationIndex = argument.indexOf("/duration");
        if (byIndex < 0) {
            throw new DamienException("A deadline needs a /by field, for example: "
                    + "deadline return book /by 2019-10-15.");
        }

        int firstFieldIndex = durationIndex < 0 ? byIndex : Math.min(byIndex, durationIndex);
        String description = argument.substring(0, firstFieldIndex).trim();
        String by = extractFieldValue(argument, byIndex, "/by".length(), durationIndex);
        String duration = durationIndex < 0
                ? null
                : extractFieldValue(argument, durationIndex, "/duration".length(), byIndex);
        if (description.isEmpty()) {
            throw new DamienException("A deadline needs a description before /by, for example: "
                    + "deadline return book /by 2019-10-15.");
        }
        if (by.isEmpty()) {
            throw new DamienException("A deadline needs a date or time after /by, for example: "
                    + "deadline return book /by 2019-10-15.");
        }
        try {
            return new Deadline(description, by, parseDuration(duration, "deadline"));
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
        String argument = getArgument(command, commandType);
        if (argument.contains("/duration")) {
            throw new DamienException("An event already specifies its time interval and cannot have a "
                    + "/duration field.");
        }
        int fromIndex = argument.indexOf("/from");
        int toIndex = argument.indexOf("/to", fromIndex + "/from".length());
        if (fromIndex < 0) {
            throw new DamienException("An event needs a /from field, for example: "
                    + "event meeting /from 2pm /to 4pm.");
        }
        if (toIndex < 0) {
            throw new DamienException("An event needs a /to field, for example: "
                    + "event meeting /from 2pm /to 4pm.");
        }

        String description = argument.substring(0, fromIndex).trim();
        String from = argument.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = argument.substring(toIndex + "/to".length()).trim();
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
     * Splits a ToDo argument into its description and optional duration field.
     *
     * @param argument the text after the ToDo command keyword
     * @return the extracted task details
     */
    private TaskDetails parseTaskDetails(String argument) {
        int durationIndex = argument.indexOf("/duration");
        if (durationIndex < 0) {
            return new TaskDetails(argument.trim(), null);
        }
        String description = argument.substring(0, durationIndex).trim();
        String duration = argument.substring(durationIndex + "/duration".length()).trim();
        return new TaskDetails(description, duration);
    }

    /**
     * Extracts one field's value while allowing the two deadline fields in either order.
     *
     * @param argument the deadline command text
     * @param fieldIndex the index where the requested field begins
     * @param fieldLength the requested field marker's length
     * @param otherFieldIndex the other field's index, or a negative value when absent
     * @return the trimmed text belonging to the requested field
     */
    private String extractFieldValue(String argument, int fieldIndex, int fieldLength,
                                     int otherFieldIndex) {
        int endIndex = otherFieldIndex > fieldIndex ? otherFieldIndex : argument.length();
        return argument.substring(fieldIndex + fieldLength, endIndex).trim();
    }

    /**
     * Parses an optional task duration and converts invalid input into a user-facing error.
     *
     * @param durationText the text after the optional {@code /duration} field
     * @param commandName the command used in an error example
     * @return the parsed duration, or {@code null} when no duration was supplied
     * @throws DamienException if a supplied duration is missing or invalid
     */
    private Duration parseDuration(String durationText, String commandName) throws DamienException {
        if (durationText == null) {
            return null;
        }
        if (durationText.isEmpty()) {
            throw new DamienException("A duration needs a value after /duration, for example: "
                    + commandName + " read report /duration 2h.");
        }
        try {
            return Duration.parse(durationText);
        } catch (IllegalArgumentException exception) {
            throw new DamienException("A duration must be a positive whole number of hours and/or "
                    + "minutes, for example: " + commandName + " read report /duration 2h.");
        }
    }

    /**
     * Extracts the text after a command keyword.
     *
     * @param command the complete command
     * @param commandType the command type identified from the input
     * @return the trimmed command argument
     */
    private String getArgument(String command, CommandType commandType) {
        assert command != null && commandType != null
                && command.startsWith(commandType.getKeyword())
                : "The parser must extract arguments from a matching command.";
        return command.substring(commandType.getKeyword().length()).trim();
    }

    /** Holds the description and optional duration extracted from a task command. */
    private static class TaskDetails {
        /** The non-field portion that describes the task. */
        private final String description;

        /** The optional text after {@code /duration}; {@code null} when absent. */
        private final String duration;

        /**
         * Creates extracted task details.
         *
         * @param description the task description
         * @param duration the optional duration text
         */
        TaskDetails(String description, String duration) {
            this.description = description;
            this.duration = duration;
        }
    }
}
