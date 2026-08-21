import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Checks the user-visible behaviour of the CHOO command-line interface.
 */
public class CHOOTest {
    /**
     * Runs a complete interaction and checks that tasks can be marked and unmarked.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        String input = "read book\nreturn book\nmark 2\nlist\nunmark 2\nlist\nbye\n";
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
        assertContains(actualOutput, "added: read book");
        assertContains(actualOutput, "added: return book");
        assertContains(actualOutput, "Nice! I've marked this task as done:");
        assertContains(actualOutput, "2.[X] return book");
        assertContains(actualOutput, "OK, I've marked this task as not done yet:");
        assertContains(actualOutput, "2.[ ] return book");
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
