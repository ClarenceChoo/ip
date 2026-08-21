import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Checks the user-visible behavior of the CHOO command-line interface.
 */
public class CHOOTest {
    /**
     * Runs an interaction that mixes invalid commands, typed tasks, and deletion.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        String input = "todo\n"
                + "mystery command\n"
                + "todo keep this\n"
                + "deadline remove this /by Friday\n"
                + "event keep event /from Monday /to Tuesday\n"
                + "delete 2\n"
                + "list\nbye\n";
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
        assertContains(actualOutput, "OOPS!!! A todo needs a description.");
        assertContains(actualOutput, "OOPS!!! I don't recognize that command.");
        assertContains(actualOutput, "Noted. I've removed this task:");
        assertContains(actualOutput, "[D][ ] remove this (by: Friday)");
        assertContains(actualOutput, "Now you have 2 tasks in the list.");
        assertContains(actualOutput, "Here are the tasks in your list:\n"
                + "1.[T][ ] keep this\n"
                + "2.[E][ ] keep event (from: Monday to: Tuesday)\n"
                + "____________________________________________________________");
        assertDoesNotContain(actualOutput, "[T][ ] mystery command");
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

    private static void assertDoesNotContain(String actual, String unexpected) {
        if (actual.contains(unexpected)) {
            throw new AssertionError("Expected output not to contain: " + unexpected
                    + System.lineSeparator() + "Actual output:" + System.lineSeparator() + actual);
        }
    }
}
