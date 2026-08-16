package duke.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a positive amount of time required to complete a task.
 *
 * <p>Durations are stored as whole minutes and displayed in a compact
 * hours-and-minutes form, such as {@code 2h} or {@code 1h 30m}.</p>
 */
public final class Duration {
    /** Recognises supported whole-hour and whole-minute duration forms. */
    private static final Pattern DURATION_PATTERN = Pattern.compile(
            "(?i)^(?:(\\d+)\\s*(?:hours?|hrs?|h)(?:\\s*(\\d+)\\s*"
                    + "(?:minutes?|mins?|m))?|(\\d+)\\s*(?:minutes?|mins?|m))$");

    /** The duration expressed as a positive number of minutes. */
    private final int minutes;

    /**
     * Creates a duration from a positive number of whole minutes.
     *
     * @param minutes the required time in whole minutes
     * @throws IllegalArgumentException if the duration is not positive
     */
    public Duration(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("A duration must be positive.");
        }
        this.minutes = minutes;
    }

    /**
     * Parses a supported duration into whole minutes.
     *
     * <p>Accepted examples include {@code 2h}, {@code 2 hours}, {@code 90m},
     * and {@code 1h 30m}. Decimal durations are not supported.</p>
     *
     * @param value the duration text entered by the user or read from storage
     * @return the parsed duration
     * @throws IllegalArgumentException if the value is not a positive supported duration
     */
    public static Duration parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("A duration must not be null.");
        }
        Matcher matcher = DURATION_PATTERN.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unsupported duration: " + value);
        }

        try {
            long hours = matcher.group(1) == null ? 0 : Long.parseLong(matcher.group(1));
            long minutePart = matcher.group(2) == null
                    ? (matcher.group(3) == null ? 0 : Long.parseLong(matcher.group(3)))
                    : Long.parseLong(matcher.group(2));
            long totalMinutes = Math.addExact(Math.multiplyExact(hours, 60), minutePart);
            if (totalMinutes <= 0 || totalMinutes > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Unsupported duration: " + value);
            }
            return new Duration((int) totalMinutes);
        } catch (NumberFormatException | ArithmeticException exception) {
            throw new IllegalArgumentException("Unsupported duration: " + value, exception);
        }
    }

    /**
     * Returns this duration as whole minutes for persistent storage.
     *
     * @return the positive number of minutes
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Returns this duration in its compact user-facing form.
     *
     * @return the duration formatted as hours and optional remaining minutes
     */
    @Override
    public String toString() {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        if (hours == 0) {
            return remainingMinutes + "m";
        }
        if (remainingMinutes == 0) {
            return hours + "h";
        }
        return hours + "h " + remainingMinutes + "m";
    }
}
