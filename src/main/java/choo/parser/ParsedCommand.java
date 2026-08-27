package choo.parser;

import choo.task.Task;

/**
 * Carries a parsed command and the data needed to execute it.
 */
public class ParsedCommand {
    private final CommandType type;
    private final int taskNumber;
    private final Task task;

    private ParsedCommand(CommandType type, int taskNumber, Task task) {
        this.type = type;
        this.taskNumber = taskNumber;
        this.task = task;
    }

    /**
     * Creates a command that has no additional data.
     *
     * @param type command type.
     * @return parsed command.
     */
    public static ParsedCommand withoutDetails(CommandType type) {
        return new ParsedCommand(type, 0, null);
    }

    /**
     * Creates a command that targets a task number.
     *
     * @param type command type.
     * @param taskNumber one-based task number.
     * @return parsed command.
     */
    public static ParsedCommand forTaskNumber(CommandType type, int taskNumber) {
        return new ParsedCommand(type, taskNumber, null);
    }

    /**
     * Creates a command that adds a task.
     *
     * @param task task to add.
     * @return parsed command.
     */
    public static ParsedCommand forNewTask(Task task) {
        return new ParsedCommand(CommandType.ADD, 0, task);
    }

    /**
     * Returns the command type.
     *
     * @return command type.
     */
    public CommandType getType() {
        return this.type;
    }

    /**
     * Returns the one-based task number for a task-targeting command.
     *
     * @return one-based task number.
     */
    public int getTaskNumber() {
        return this.taskNumber;
    }

    /**
     * Returns the task carried by an add command.
     *
     * @return task to add.
     */
    public Task getTask() {
        return this.task;
    }
}
