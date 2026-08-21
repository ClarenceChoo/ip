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
        Task deadline = new Deadline("return book", "Sunday");
        Task event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("[T][ ] borrow book", todo.toString());
        assertEquals("[D][ ] return book (by: Sunday)", deadline.toString());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());

        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Sunday)", deadline.toString());
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected
                    + System.lineSeparator() + "Actual: " + actual);
        }
    }
}
