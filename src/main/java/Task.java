/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String typeMarker;
    private final String description;
    private final String timingDetails;
    private boolean isDone;

    /**
     * Creates an incomplete ToDo with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this("T", description, "");
    }

    /**
     * Creates an incomplete task with its display type and timing details.
     *
     * @param typeMarker one-letter task type shown to the user
     * @param description description of the task
     * @param timingDetails type-specific timing text, including punctuation
     */
    public Task(String typeMarker, String description, String timingDetails) {
        this.typeMarker = typeMarker;
        this.description = description;
        this.timingDetails = timingDetails;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns a symbol that indicates whether this task is complete.
     *
     * @return {@code X} when complete, or a space otherwise
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns the task in the format displayed to the user.
     *
     * @return task type, status, description, and any timing details
     */
    @Override
    public String toString() {
        return "[" + this.typeMarker + "][" + getStatusIcon() + "] "
                + this.description + this.timingDetails;
    }
}
