package choo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter
            .ofPattern("MMM d uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("MMM d uuuu, h:mma", Locale.ENGLISH);

    private final LocalDateTime by;
    private final boolean hasTime;

    /**
     * Creates an incomplete Deadline with its due date or time.
     *
     * @param description description of the task
     * @param by due date in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format
     * @throws DateTimeParseException if the date or time is invalid
     */
    public Deadline(String description, String by) {
        super(description);
        this.hasTime = by.contains(" ");
        this.by = parseBy(by);
    }

    /**
     * Returns the deadline date and time.
     *
     * @return deadline date and time
     */
    public LocalDateTime getBy() {
        return this.by;
    }

    /**
     * Returns whether the user supplied an explicit time for this deadline.
     *
     * @return true if the deadline includes a time
     */
    public boolean hasTime() {
        return this.hasTime;
    }

    /**
     * Returns this task with its Deadline type marker and due text.
     *
     * @return formatted Deadline
     */
    @Override
    public String toString() {
        DateTimeFormatter displayFormat = this.hasTime
                ? DISPLAY_DATE_TIME_FORMAT : DISPLAY_DATE_FORMAT;
        return "[D]" + super.toString() + " (by: " + this.by.format(displayFormat) + ")";
    }

    private static LocalDateTime parseBy(String by) {
        if (!by.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}( [0-9]{4})?")) {
            throw new DateTimeParseException("Invalid deadline format", by, 0);
        }
        if (by.contains(" ")) {
            return LocalDateTime.parse(by, INPUT_DATE_TIME_FORMAT);
        }
        return LocalDate.parse(by, INPUT_DATE_FORMAT).atStartOfDay();
    }
}
