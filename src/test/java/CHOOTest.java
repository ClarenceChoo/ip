import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Checks the user-visible behavior of the CHOO command-line interface.
 */
public class CHOOTest {
    /**
     * Runs a complete interaction with all Level 4 task types.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        String input = "todo borrow book\n"
                + "deadline return book /by Sunday\n"
                + "deadline do homework /by no idea :-p\n"
                + "event project meeting /from Mon 2pm /to 4pm\n"
                + "mark 2\nlist\nbye\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            CHOO.main(new String[0]);
        } finally {
            System.setOut(originalOutput);
        }

        String actualOutput = output.toString(StandardCharsets.UTF_8);
        assertContains(actualOutput, "[T][ ] borrow book");
        assertContains(actualOutput, "[D][ ] return book (by: Sunday)");
        assertContains(actualOutput, "[D][ ] do homework (by: no idea :-p)");
        assertContains(actualOutput, "[E][ ] project meeting (from: Mon 2pm to: 4pm)");
        assertContains(actualOutput, "Now you have 4 tasks in the list.");
        assertContains(actualOutput, "Nice! I've marked this task as done:");
        assertContains(actualOutput, "2.[D][X] return book (by: Sunday)");
        assertContains(actualOutput, "Bye. Hope to see you again soon!");
    }

    /**
     * Reports a helpful failure when expected user-visible text is absent.
     *
     * @param actual complete application output
     * @param expected expected portion of the output
     */
    private static void assertContains(String actual, String expected) {
        if (!actual.contains(expected)) {
            throw new AssertionError("Expected output to contain: " + expected
                    + System.lineSeparator() + "Actual output:" + System.lineSeparator() + actual);
        }
    }
}
