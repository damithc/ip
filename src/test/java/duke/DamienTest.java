package duke;

import java.nio.file.Path;

import duke.ui.GuiUi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the command-processing boundary shared by the GUI and the CLI.
 */
public class DamienTest {
    /** Temporary directory used for the test application's task data. */
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that GUI output contains responses from the existing command
     * processing logic and that {@code bye} terminates the session.
     */
    @Test
    public void guiUiReceivesCommandResponsesAndByeStopsSession() {
        GuiUi guiUi = new GuiUi();
        Damien damien = new Damien(temporaryDirectory.resolve("tasks.txt").toString(), guiUi);

        damien.showWelcome();
        assertTrue(guiUi.consumeOutput().contains("Hello! I'm Damien"));

        assertTrue(damien.processCommand("todo read book"));
        String addResponse = guiUi.consumeOutput();
        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(addResponse.contains("Now you have 1 tasks in the list."));

        assertTrue(damien.processCommand("list"));
        assertTrue(guiUi.consumeOutput().contains("1.[T][ ] read book"));

        assertFalse(damien.processCommand("bye"));
        assertTrue(guiUi.consumeOutput().contains("Bye. Hope to see you again soon!"));
    }
}
