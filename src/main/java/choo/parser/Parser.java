package choo.parser;

import choo.exception.ChooException;
import choo.task.Deadline;
import choo.task.Event;
import choo.task.Todo;

import java.time.format.DateTimeParseException;

/**
 * Converts raw user input into structured commands.
 */
public class Parser {
    private Parser() {
    }

    /**
     * Parses a complete command line.
     *
     * @param command raw user command
     * @return structured command
     * @throws ChooException if the command or its details are invalid
     */
    public static ParsedCommand parse(String command) throws ChooException {
        String trimmedCommand = command.trim();
        if (trimmedCommand.equals("bye")) {
            return ParsedCommand.withoutDetails(CommandType.BYE);
        }
        if (trimmedCommand.equals("list")) {
            return ParsedCommand.withoutDetails(CommandType.LIST);
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

    private static ParsedCommand parseTodo(String command) throws ChooException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ChooException("A todo needs a description.");
        }
        return ParsedCommand.forNewTask(new Todo(description));
    }

    private static ParsedCommand parseDeadline(String command) throws ChooException {
        String taskDetails = command.substring("deadline".length()).trim();
        if (taskDetails.isEmpty()) {
            throw new ChooException("A deadline needs a description.");
        }

        int byIndex = taskDetails.indexOf("/by");
        if (byIndex < 0) {
            throw new ChooException("A deadline needs a /by date or time.");
        }

        String description = taskDetails.substring(0, byIndex).trim();
        String by = taskDetails.substring(byIndex + 3).trim();
        if (description.isEmpty()) {
            throw new ChooException("A deadline needs a description.");
        }
        if (by.isEmpty()) {
            throw new ChooException("A deadline needs a /by date or time.");
        }
        try {
            return ParsedCommand.forNewTask(new Deadline(description, by));
        } catch (DateTimeParseException exception) {
            throw new ChooException(
                    "Use yyyy-MM-dd or yyyy-MM-dd HHmm for a deadline date.");
        }
    }

    private static ParsedCommand parseEvent(String command) throws ChooException {
        String taskDetails = command.substring("event".length()).trim();
        if (taskDetails.isEmpty()) {
            throw new ChooException("An event needs a description.");
        }

        int fromIndex = taskDetails.indexOf("/from");
        int toIndex = taskDetails.indexOf("/to");
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
            throw new ChooException("An event needs both /from and /to values.");
        }

        String description = taskDetails.substring(0, fromIndex).trim();
        String from = taskDetails.substring(fromIndex + 5, toIndex).trim();
        String to = taskDetails.substring(toIndex + 3).trim();
        if (description.isEmpty()) {
            throw new ChooException("An event needs a description.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new ChooException("An event needs both /from and /to values.");
        }
        return ParsedCommand.forNewTask(new Event(description, from, to));
    }
}
