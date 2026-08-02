package duke.parser;

import java.time.LocalDateTime;

import duke.command.Command;
import duke.command.CommandType;
import duke.exception.DamienException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Todo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the command and task extraction performed by {@link Parser}.
 */
public class ParserTest {
    /** The parser used by the test cases. */
    private final Parser parser = new Parser();

    /**
     * Verifies that a todo command produces a todo with the right description.
     *
     * @throws DamienException if the valid command cannot be parsed
     */
    @Test
    public void parseTodoCommandCreatesTodoTask() throws DamienException {
        Command command = parser.parse("todo read book");

        assertEquals(CommandType.TODO, command.getType());
        Todo todo = assertInstanceOf(Todo.class, command.getTask());
        assertEquals("read book", todo.getDescription());
    }

    /**
     * Verifies that a deadline command separates its description and deadline.
     *
     * @throws DamienException if the valid command cannot be parsed
     */
    @Test
    public void parseDeadlineCommandExtractsDeadlineDetails() throws DamienException {
        Command command = parser.parse("deadline return book /by 2019-12-02");

        assertEquals(CommandType.DEADLINE, command.getType());
        Deadline deadline = assertInstanceOf(Deadline.class, command.getTask());
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
    }

    /**
     * Verifies that a deadline command also accepts the original date-and-time example.
     *
     * @throws DamienException if the valid command cannot be parsed
     */
    @Test
    public void parseDeadlineCommandExtractsDateAndTime() throws DamienException {
        Command command = parser.parse("deadline return book /by 2/12/2019 1800");

        Deadline deadline = assertInstanceOf(Deadline.class, command.getTask());

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)", deadline.toString());
    }

    /**
     * Verifies that an invalid deadline date is rejected with a useful message.
     */
    @Test
    public void parseDeadlineCommandRejectsInvalidDate() {
        DamienException exception = assertThrows(DamienException.class,
                () -> parser.parse("deadline return book /by 2019-02-30"));

        assertEquals("A deadline date must use yyyy-MM-dd, optionally followed by HHmm, "
                + "or d/M/yyyy HHmm, for example: deadline return book /by 2019-10-15.",
                exception.getMessage());
    }

    /**
     * Verifies that an event command extracts both ends of its time interval.
     *
     * @throws DamienException if the valid command cannot be parsed
     */
    @Test
    public void parseEventCommandExtractsEventDetails() throws DamienException {
        Command command = parser.parse("event project meeting /from Mon 2pm /to 4pm");

        assertEquals(CommandType.EVENT, command.getType());
        Event event = assertInstanceOf(Event.class, command.getTask());
        assertEquals("project meeting", event.getDescription());
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    /**
     * Verifies that task numbers are converted from one-based input to indexes.
     *
     * @throws DamienException if the valid command cannot be parsed
     */
    @Test
    public void parseMarkCommandConvertsTaskNumberToZeroBasedIndex() throws DamienException {
        Command command = parser.parse("mark 3");

        assertEquals(CommandType.MARK, command.getType());
        assertEquals(2, command.getTaskIndex());
    }

    /**
     * Verifies that a find command preserves its search keyword.
     *
     * @throws DamienException if the valid command cannot be parsed
     */
    @Test
    public void parseFindCommandExtractsKeyword() throws DamienException {
        Command command = parser.parse("find book");

        assertEquals(CommandType.FIND, command.getType());
        assertEquals("book", command.getKeyword());
        assertNull(command.getTaskIndex());
        assertNull(command.getTask());
    }

    /**
     * Verifies that a find command requires a keyword.
     */
    @Test
    public void parseFindCommandRejectsMissingKeyword() {
        DamienException exception = assertThrows(DamienException.class,
                () -> parser.parse("find"));

        assertEquals("Please provide a keyword after find, for example: find book.",
                exception.getMessage());
    }

    /**
     * Verifies that commands without arguments are still recognised.
     *
     * @throws DamienException if the valid command cannot be parsed
     */
    @Test
    public void parseByeCommandCreatesCommandWithoutTaskData() throws DamienException {
        Command command = parser.parse("bye");

        assertEquals(CommandType.BYE, command.getType());
        assertNull(command.getTaskIndex());
        assertNull(command.getTask());
    }

    /**
     * Verifies that an invalid task number produces a user-facing exception.
     */
    @Test
    public void parseRejectsNonPositiveTaskNumbers() {
        DamienException exception = assertThrows(DamienException.class,
                () -> parser.parse("mark 0"));

        assertEquals("Task numbers start at 1. Use list to see valid task numbers.",
                exception.getMessage());
    }

    /**
     * Verifies that an unknown command produces a user-facing exception.
     */
    @Test
    public void parseRejectsUnknownCommands() {
        DamienException exception = assertThrows(DamienException.class,
                () -> parser.parse("what is this"));

        assertEquals("I'm sorry, but I don't know what that means :-(",
                exception.getMessage());
    }
}
