package choo.task;

import java.util.ArrayList;
import java.util.List;

import choo.exception.ChooException;

/**
 * Owns the task collection and its one-based position operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing a defensive copy of the supplied tasks.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Inserts a task at a one-based position.
     *
     * @param taskNumber one-based insertion position.
     * @param task task to insert.
     * @throws ChooException if the insertion position is outside the list.
     */
    public void add(int taskNumber, Task task) throws ChooException {
        if (taskNumber < 1 || taskNumber > this.tasks.size() + 1) {
            throw invalidPosition(taskNumber);
        }
        this.tasks.add(taskNumber - 1, task);
    }

    /**
     * Returns the task at a one-based position.
     *
     * @param taskNumber one-based task number.
     * @return selected task.
     * @throws ChooException if the task number is outside the list.
     */
    public Task get(int taskNumber) throws ChooException {
        validateTaskNumber(taskNumber);
        return this.tasks.get(taskNumber - 1);
    }

    /**
     * Removes and returns the task at a one-based position.
     *
     * @param taskNumber one-based task number.
     * @return removed task.
     * @throws ChooException if the task number is outside the list.
     */
    public Task remove(int taskNumber) throws ChooException {
        validateTaskNumber(taskNumber);
        return this.tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the number of tasks.
     *
     * @return task count.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the tasks.
     *
     * @return task snapshot.
     */
    public List<Task> asList() {
        return List.copyOf(this.tasks);
    }

    /**
     * Validates that a one-based task number identifies an existing task.
     *
     * @param taskNumber One-based task number to validate.
     * @throws ChooException If the task number is outside the list.
     */
    private void validateTaskNumber(int taskNumber) throws ChooException {
        if (taskNumber < 1 || taskNumber > this.tasks.size()) {
            throw invalidPosition(taskNumber);
        }
    }

    private static ChooException invalidPosition(int taskNumber) {
        return new ChooException("Task number " + taskNumber + " is outside the list.");
    }
}
