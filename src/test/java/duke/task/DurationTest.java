package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests parsing, validation, and display formatting for {@link Duration}. */
public class DurationTest {
    /** Verifies that supported hour and minute forms are normalised to whole minutes. */
    @Test
    public void parseAcceptsSupportedDurationForms() {
        assertEquals(120, Duration.parse("2 hours").getMinutes());
        assertEquals(90, Duration.parse("90m").getMinutes());
        assertEquals(90, Duration.parse("1h 30m").getMinutes());
    }

    /** Verifies that durations use a concise canonical display format. */
    @Test
    public void toStringUsesCanonicalHoursAndMinutes() {
        assertEquals("2h", new Duration(120).toString());
        assertEquals("30m", new Duration(30).toString());
        assertEquals("1h 30m", new Duration(90).toString());
    }

    /** Verifies that zero, decimals, and unrecognised duration units are rejected. */
    @Test
    public void parseRejectsUnsupportedOrNonPositiveDurations() {
        assertThrows(IllegalArgumentException.class, () -> Duration.parse("0h"));
        assertThrows(IllegalArgumentException.class, () -> Duration.parse("1.5 hours"));
        assertThrows(IllegalArgumentException.class, () -> Duration.parse("2 days"));
    }
}
