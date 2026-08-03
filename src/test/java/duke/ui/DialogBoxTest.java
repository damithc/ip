package duke.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the response classification used by the conversation styling.
 */
public class DialogBoxTest {
    /**
     * Verifies that numbered todo, deadline, and event rows are recognised.
     */
    @Test
    public void containsTaskListRows_recognisesFormattedTaskRows() {
        String response = "Here are the tasks in your list:\n"
                + "1.[T][ ] prepare report\n"
                + "2.[D][X] submit report (by: Aug 3 2026)\n"
                + "3.[E][ ] team sync (from: 10am to: 11am)";

        assertTrue(DialogBox.containsTaskListRows(response));
    }

    /**
     * Verifies that ordinary responses are not classified as task lists.
     */
    @Test
    public void containsTaskListRows_ignoresOrdinaryResponses() {
        assertFalse(DialogBox.containsTaskListRows("What can I do for you?"));
        assertFalse(DialogBox.containsTaskListRows("Got it. I've added this task:\n"
                + "  [T][ ] prepare report"));
    }
}
