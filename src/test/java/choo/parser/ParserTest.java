package choo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import choo.exception.ChooException;

/**
 * Checks conversion of raw user input into structured commands.
 */
public class ParserTest {
    @Test
    void parse_simpleCommands_returnsMatchingTypes() throws ChooException {
        assertEquals(CommandType.BYE, Parser.parse("  bye  ").getType());
        assertEquals(CommandType.LIST, Parser.parse("list").getType());
    }

    @Test
    void parse_taskNumberCommand_returnsOneBasedPosition() throws ChooException {
        ParsedCommand markCommand = Parser.parse("mark 12");
        assertEquals(CommandType.MARK, markCommand.getType());
        assertEquals(12, markCommand.getTaskNumber());
        assertEquals(CommandType.UNMARK, Parser.parse("unmark 2").getType());
        assertEquals(CommandType.DELETE, Parser.parse("delete 3").getType());
    }

    @Test
    void parse_todoCommand_returnsTodoTask() throws ChooException {
        ParsedCommand todoCommand = Parser.parse("todo read book");
        assertEquals(CommandType.ADD, todoCommand.getType());
        assertEquals("[T][ ] read book", todoCommand.getTask().toString());
    }

    @Test
    void parse_deadlineCommand_returnsFormattedDeadline() throws ChooException {
        ParsedCommand deadlineCommand = Parser.parse("deadline submit /by 2019-12-02 1800");
        assertEquals("[D][ ] submit (by: Dec 2 2019, 6:00PM)",
                deadlineCommand.getTask().toString());
    }

    @Test
    void parse_eventCommand_returnsEventWithRange() throws ChooException {
        ParsedCommand eventCommand = Parser.parse("event meeting /from Mon /to Tue");
        assertEquals("[E][ ] meeting (from: Mon to: Tue)", eventCommand.getTask().toString());
    }

    @Test
    void parse_findCommand_returnsKeyword() throws ChooException {
        ParsedCommand findCommand = Parser.parse("  find project book  ");

        assertEquals(CommandType.FIND, findCommand.getType());
        assertEquals("project book", findCommand.getKeyword());
    }

    @Test
    void parse_invalidCommands_throwsSpecificErrors() {
        assertError("mark two", "Enter a whole-number task position after mark.");
        assertError("todo", "A todo needs a description.");
        assertError("deadline report", "A deadline needs a /by date or time.");
        assertError("deadline /by 2019-12-02", "A deadline needs a description.");
        assertError("deadline report /by Friday",
                "Use yyyy-MM-dd or yyyy-MM-dd HHmm for a deadline date.");
        assertError("event meeting /from Mon", "An event needs both /from and /to values.");
        assertError("find", "A find command needs a keyword.");
        assertError("mystery", "I don't recognize that command.");
    }

    private static void assertError(String input, String expectedMessage) {
        ChooException exception = assertThrows(ChooException.class,
                () -> Parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
