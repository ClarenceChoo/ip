/**
 * Checks conversion of raw user input into structured commands.
 */
public class ParserTest {
    /**
     * Runs parser checks for valid commands and representative errors.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) throws ChooException {
        assertEquals(CommandType.BYE, Parser.parse("  bye  ").getType());
        assertEquals(CommandType.LIST, Parser.parse("list").getType());

        ParsedCommand mark = Parser.parse("mark 12");
        assertEquals(CommandType.MARK, mark.getType());
        assertEquals(12, mark.getTaskNumber());

        ParsedCommand todo = Parser.parse("todo read book");
        assertEquals(CommandType.ADD, todo.getType());
        assertEquals("[T][ ] read book", todo.getTask().toString());

        ParsedCommand deadline = Parser.parse("deadline submit /by 2019-12-02 1800");
        assertEquals("[D][ ] submit (by: Dec 2 2019, 6:00PM)",
                deadline.getTask().toString());

        ParsedCommand event = Parser.parse("event meeting /from Mon /to Tue");
        assertEquals("[E][ ] meeting (from: Mon to: Tue)", event.getTask().toString());

        assertError("mark two", "Enter a whole-number task position after mark.");
        assertError("todo", "A todo needs a description.");
        assertError("deadline report /by Friday",
                "Use yyyy-MM-dd or yyyy-MM-dd HHmm for a deadline date.");
        assertError("event meeting /from Mon", "An event needs both /from and /to values.");
        assertError("mystery", "I don't recognize that command.");
    }

    private static void assertError(String input, String expectedMessage) {
        try {
            Parser.parse(input);
            throw new AssertionError("Expected command to be rejected: " + input);
        } catch (ChooException exception) {
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", actual: " + actual);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected
                    + System.lineSeparator() + "Actual: " + actual);
        }
    }
}
