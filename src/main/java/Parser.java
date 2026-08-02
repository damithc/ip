/**
 * Interprets user commands and extracts the information needed to execute them.
 */
public class Parser {
    /**
     * Identifies the command represented by a complete input line.
     *
     * @param input the complete line entered by the user
     * @return the command type represented by the input
     * @throws DamienException if the input is not a recognised command
     */
    public CommandType parseCommand(String input) throws DamienException {
        CommandType commandType = CommandType.fromInput(input);
        if (commandType == null) {
            throw new DamienException("I'm sorry, but I don't know what that means :-(");
        }
        return commandType;
    }

    /**
     * Extracts and validates the description of a ToDo command.
     *
     * @param command the complete ToDo command
     * @param commandType the command type identified from the input
     * @return the non-empty ToDo description
     * @throws DamienException if the description is empty
     */
    public String parseTodoDescription(String command, CommandType commandType)
            throws DamienException {
        String description = getArgument(command, commandType);
        if (description.isEmpty()) {
            throw new DamienException("The description of a todo cannot be empty.");
        }
        return description;
    }

    /**
     * Converts a task number in a command into a zero-based task index.
     *
     * @param command the command containing the task number
     * @param commandType the command type identified from the input
     * @return the zero-based task index
     * @throws DamienException if the command does not contain a positive integer
     */
    public int parseTaskIndex(String command, CommandType commandType) throws DamienException {
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
    public Task parseDeadline(String command, CommandType commandType) throws DamienException {
        command = getArgument(command, commandType);
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

    /**
     * Parses the part of an event command after the command keyword.
     *
     * @param command the complete event command
     * @param commandType the command type identified from the input
     * @return the parsed event task
     * @throws DamienException if the description, /from field, or /to field is missing
     */
    public Task parseEvent(String command, CommandType commandType) throws DamienException {
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
