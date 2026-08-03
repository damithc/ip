package duke.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Captures Damien's formatted responses so that the JavaFX controller can
 * display them as chatbot messages.
 */
public class GuiUi extends Ui {
    /** Stores output generated since the last response was consumed. */
    private final ByteArrayOutputStream output;

    /**
     * Creates a response-capturing interface for the GUI.
     */
    public GuiUi() {
        this(new ByteArrayOutputStream());
    }

    /**
     * Creates an interface using the given response buffer.
     *
     * @param output the buffer that receives formatted responses
     */
    private GuiUi(ByteArrayOutputStream output) {
        super(new PrintStream(output, true, StandardCharsets.UTF_8));
        this.output = output;
    }

    /**
     * Returns all output generated since the previous call and clears it.
     *
     * @return the captured response without trailing line breaks
     */
    public String consumeOutput() {
        String response = output.toString(StandardCharsets.UTF_8);
        output.reset();
        return response.stripTrailing();
    }
}
