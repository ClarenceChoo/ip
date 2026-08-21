/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private TaskStatus status;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.status = TaskStatus.DONE;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns a symbol that indicates whether this task is complete.
     *
     * @return {@code X} when complete, or a space otherwise
     */
    public String getStatusIcon() {
        return this.status.getIcon();
    }

    /**
     * Returns the task in the format displayed to the user.
     *
     * @return task status followed by its description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }
}
