package choo.parser;

import java.time.format.DateTimeParseException;

import choo.exception.ChooException;
import choo.task.Deadline;
import choo.task.Event;
import choo.task.Todo;

/**
 * Converts raw user input into structured commands.
 */
public class Parser {
    private Parser() {
    }

    /**
     * Parses a complete command line.
     *
     * @param command raw user command.
     * @return structured command.
     * @throws ChooException if the command or its details are invalid.
     */
    public static ParsedCommand parse(String command) throws ChooException {
        String trimmedCommand = command.trim();
        if (trimmedCommand.equals("bye")) {
            return ParsedCommand.withoutDetails(CommandType.BYE);
        }
        if (trimmedCommand.equals("list")) {
            return ParsedCommand.withoutDetails(CommandType.LIST);
        }
        if (trimmedCommand.equals("sort")) {
            return ParsedCommand.withoutDetails(CommandType.SORT);
        }
        if (isCommand(trimmedCommand, "mark")) {
            return parseTaskNumber(trimmedCommand, "mark", CommandType.MARK);
        }
        if (isCommand(trimmedCommand, "unmark")) {
            return parseTaskNumber(trimmedCommand, "unmark", CommandType.UNMARK);
        }
        if (isCommand(trimmedCommand, "delete")) {
            return parseTaskNumber(trimmedCommand, "delete", CommandType.DELETE);
        }
        if (isCommand(trimmedCommand, "find")) {
            return parseFind(trimmedCommand);
        }
        if (isCommand(trimmedCommand, "todo")) {
            return parseTodo(trimmedCommand);
        }
        if (isCommand(trimmedCommand, "deadline")) {
            return parseDeadline(trimmedCommand);
        }
        if (isCommand(trimmedCommand, "event")) {
            return parseEvent(trimmedCommand);
        }
        throw new ChooException("I don't recognize that command.");
    }

    private static boolean isCommand(String command, String keyword) {
        return command.equals(keyword) || command.startsWith(keyword + " ");
    }

    /**
     * Parses the one-based task position following a command keyword.
     *
     * @param command Complete trimmed command.
     * @param keyword Command keyword preceding the position.
     * @param type Command type to include in the result.
     * @return Parsed task-targeting command.
     * @throws ChooException If the position is not a whole number.
     */
    private static ParsedCommand parseTaskNumber(String command, String keyword,
            CommandType type) throws ChooException {
        String taskNumberText = command.substring(keyword.length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            return ParsedCommand.forTaskNumber(type, taskNumber);
        } catch (NumberFormatException exception) {
            throw new ChooException(
                    "Enter a whole-number task position after " + keyword + ".");
        }
    }

    /**
     * Parses a todo command and validates its description.
     *
     * @param command Complete trimmed todo command.
     * @return Parsed add command carrying a todo.
     * @throws ChooException If the description is empty.
     */
    private static ParsedCommand parseTodo(String command) throws ChooException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ChooException("A todo needs a description.");
        }
        return ParsedCommand.forNewTask(new Todo(description));
    }

    /**
     * Parses a find command and validates its keyword.
     *
     * @param command Complete trimmed find command.
     * @return Parsed find command carrying a keyword.
     * @throws ChooException If the keyword is empty.
     */
    private static ParsedCommand parseFind(String command) throws ChooException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new ChooException("A find command needs a keyword.");
        }
        return ParsedCommand.forKeyword(keyword);
    }

    /**
     * Parses a deadline command and validates its description and due value.
     *
     * @param command Complete trimmed deadline command.
     * @return Parsed add command carrying a deadline.
     * @throws ChooException If required details or a valid date are missing.
     */
    private static ParsedCommand parseDeadline(String command) throws ChooException {
        String taskDetails = command.substring("deadline".length()).trim();
        if (taskDetails.isEmpty()) {
            throw new ChooException("A deadline needs a description.");
        }

        int byDelimiterIndex = taskDetails.indexOf("/by");
        if (byDelimiterIndex < 0) {
            throw new ChooException("A deadline needs a /by date or time.");
        }

        String description = taskDetails.substring(0, byDelimiterIndex).trim();
        String dueDateTimeText = taskDetails.substring(byDelimiterIndex + 3).trim();
        if (description.isEmpty()) {
            throw new ChooException("A deadline needs a description.");
        }
        if (dueDateTimeText.isEmpty()) {
            throw new ChooException("A deadline needs a /by date or time.");
        }
        try {
            return ParsedCommand.forNewTask(new Deadline(description, dueDateTimeText));
        } catch (DateTimeParseException exception) {
            throw new ChooException(
                    "Use yyyy-MM-dd or yyyy-MM-dd HHmm for a deadline date.");
        }
    }

    /**
     * Parses an event command and validates its description and time range.
     *
     * @param command Complete trimmed event command.
     * @return Parsed add command carrying an event.
     * @throws ChooException If required details are missing or misplaced.
     */
    private static ParsedCommand parseEvent(String command) throws ChooException {
        String taskDetails = command.substring("event".length()).trim();
        if (taskDetails.isEmpty()) {
            throw new ChooException("An event needs a description.");
        }

        int fromDelimiterIndex = taskDetails.indexOf("/from");
        int toDelimiterIndex = taskDetails.indexOf("/to");
        if (fromDelimiterIndex < 0 || toDelimiterIndex < 0
                || toDelimiterIndex <= fromDelimiterIndex) {
            throw new ChooException("An event needs both /from and /to values.");
        }

        String description = taskDetails.substring(0, fromDelimiterIndex).trim();
        String startText = taskDetails.substring(fromDelimiterIndex + 5, toDelimiterIndex).trim();
        String endText = taskDetails.substring(toDelimiterIndex + 3).trim();
        if (description.isEmpty()) {
            throw new ChooException("An event needs a description.");
        }
        if (startText.isEmpty() || endText.isEmpty()) {
            throw new ChooException("An event needs both /from and /to values.");
        }
        return ParsedCommand.forNewTask(new Event(description, startText, endText));
    }
}
