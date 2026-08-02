package duke.command;

/**
 * Represents the fixed set of commands Damien understands.
 * Each command stores the keyword a user types and whether it can have an argument.
 */
public enum CommandType {
    BYE("bye", false),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true);

    /** The keyword used to invoke this command. */
    private final String keyword;

    /** Whether this command may be followed by an argument. */
    private final boolean acceptsArgument;

    /**
     * Creates a command type with its input keyword and argument rule.
     *
     * @param keyword the word a user types to invoke the command
     * @param acceptsArgument whether the command can be followed by an argument
     */
    CommandType(String keyword, boolean acceptsArgument) {
        this.keyword = keyword;
        this.acceptsArgument = acceptsArgument;
    }

    /**
     * Finds the command type represented by an input line.
     *
     * @param input the complete line entered by the user
     * @return the matching command type, or {@code null} when the input is unknown
     */
    public static CommandType fromInput(String input) {
        for (CommandType commandType : values()) {
            if (commandType.matches(input)) {
                return commandType;
            }
        }
        return null;
    }

    /**
     * Returns this command's keyword.
     *
     * @return the command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Checks whether an input line invokes this command type.
     *
     * @param input the complete line entered by the user
     * @return whether the input invokes this command
     */
    private boolean matches(String input) {
        return input.equals(keyword) || (acceptsArgument && input.startsWith(keyword + " "));
    }
}
