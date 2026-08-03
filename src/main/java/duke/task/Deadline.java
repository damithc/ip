package duke.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * A task that must be completed by a specified date and optional time.
 */
public class Deadline extends Task {
    /** Formats a date for display without a time. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);

    /** Formats a date and time for display. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);

    /** Formats a date for persistence. */
    private static final DateTimeFormatter STORAGE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Formats a date and time for persistence. */
    private static final DateTimeFormatter STORAGE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Accepts the date-and-time form used in saved records and commands. */
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Accepts the date-and-time form from the original Level 8 example. */
    private static final DateTimeFormatter DAY_MONTH_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** The date and optional time by which this task should be completed. */
    private final LocalDateTime by;

    /** Whether the user supplied a time as part of the deadline. */
    private final boolean hasTime;

    /**
     * Creates a date-only deadline task.
     *
     * @param description the work to be completed
     * @param by the date by which the work should be completed
     */
    public Deadline(String description, LocalDate by) {
        this(description, by.atStartOfDay(), false);
    }

    /**
     * Creates a deadline task with a date and time.
     *
     * @param description the work to be completed
     * @param by the date and time by which the work should be completed
     */
    public Deadline(String description, LocalDateTime by) {
        this(description, by, true);
    }

    /**
     * Creates a deadline task from a supported user or storage representation.
     *
     * @param description the work to be completed
     * @param by the deadline in {@code yyyy-MM-dd}, {@code yyyy-MM-dd HHmm}, or
     *        {@code d/M/yyyy HHmm} format
     * @throws IllegalArgumentException if the deadline does not use a supported format
     */
    public Deadline(String description, String by) {
        this(description, parseDateTime(by), hasTime(by));
    }

    /**
     * Creates a deadline task with its parsed value and display mode.
     *
     * @param description the work to be completed
     * @param by the parsed date and optional time
     * @param hasTime whether the original value included a time
     */
    private Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        assert by != null : "A deadline must have a parsed date and time.";
        this.by = by;
        this.hasTime = hasTime;
    }

    /**
     * Returns the date and optional time by which this task should be completed.
     *
     * @return the typed deadline value
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns the date portion of this deadline.
     *
     * @return the deadline date
     */
    public LocalDate getDate() {
        return by.toLocalDate();
    }

    @Override
    public String toStorageString() {
        String status = isDone() ? "1" : "0";
        String storedDeadline = hasTime
                ? STORAGE_DATE_TIME_FORMATTER.format(by)
                : STORAGE_DATE_FORMATTER.format(by);
        return "D | " + status + " | " + getDescription() + " | " + storedDeadline;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = hasTime
                ? DISPLAY_DATE_TIME_FORMATTER
                : DISPLAY_DATE_FORMATTER;
        return "[D]" + super.toString() + " (by: " + formatter.format(by) + ")";
    }

    /**
     * Parses a supported deadline representation into a typed date and time.
     *
     * @param value the deadline text
     * @return the parsed deadline at midnight when no time is supplied
     * @throws IllegalArgumentException if the value is not a supported date format
     */
    private static LocalDateTime parseDateTime(String value) {
        assert value != null : "A deadline value must not be null.";
        try {
            return LocalDateTime.parse(value, ISO_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDateTime.parse(value, DAY_MONTH_DATE_TIME_FORMATTER);
            } catch (DateTimeParseException ignored) {
                try {
                    return LocalDate.parse(value, STORAGE_DATE_FORMATTER).atStartOfDay();
                } catch (DateTimeParseException invalidDate) {
                    throw new IllegalArgumentException("Unsupported deadline date: " + value,
                            invalidDate);
                }
            }
        }
    }

    /**
     * Checks whether a deadline representation includes a time.
     *
     * @param value the deadline text
     * @return true when the value includes a time
     */
    private static boolean hasTime(String value) {
        try {
            LocalDate.parse(value, STORAGE_DATE_FORMATTER);
            return false;
        } catch (DateTimeParseException exception) {
            return true;
        }
    }
}
