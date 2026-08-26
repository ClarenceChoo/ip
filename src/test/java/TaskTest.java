import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Checks the shared and type-specific behavior of the task hierarchy.
 */
public class TaskTest {
    /**
     * Creates each subtype through the common {@link Task} type and checks
     * its user-visible representation.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Task todo = new Todo("borrow book");
        Deadline deadline = new Deadline("return book", "2019-12-02");
        Deadline timedDeadline = new Deadline("submit report", "2019-12-02 1800");
        Deadline midnightDeadline = new Deadline("start day", "2019-12-02 0000");
        Deadline leapDayDeadline = new Deadline("leap day", "2020-02-29 2359");
        Task event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("[T][ ] borrow book", todo.toString());
        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
        assertEquals("[D][ ] submit report (by: Dec 2 2019, 6:00PM)",
                timedDeadline.toString());
        assertEquals("[D][ ] start day (by: Dec 2 2019, 12:00AM)",
                midnightDeadline.toString());
        assertEquals("[D][ ] leap day (by: Feb 29 2020, 11:59PM)",
                leapDayDeadline.toString());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getBy());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());

        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Dec 2 2019)", deadline.toString());

        assertInvalidDeadline("2019-12-02 2400");
        assertInvalidDeadline("2019-12-02 2360");
        assertInvalidDeadline("2019-1-02");
        assertInvalidDeadline("+12345-01-01");
        assertInvalidDeadline("-0001-01-01");
    }

    private static void assertInvalidDeadline(String by) {
        try {
            new Deadline("invalid", by);
            throw new AssertionError("Expected an invalid deadline: " + by);
        } catch (DateTimeParseException expected) {
            // Expected: invalid date syntax and values must be rejected.
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected
                    + System.lineSeparator() + "Actual: " + actual);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected
                    + System.lineSeparator() + "Actual: " + actual);
        }
    }
}
