package duke.command;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the assumptions enforced when parsed commands are constructed.
 */
public class CommandTest {
    /**
     * Verifies that Java assertions reject a task command without its task index.
     */
    @Test
    public void constructorRejectsMissingTaskIndexWhenAssertionsAreEnabled() {
        assertThrows(AssertionError.class, () -> new Command(CommandType.MARK));
    }
}
