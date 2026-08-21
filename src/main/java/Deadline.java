/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete Deadline with its due date or time.
     *
     * @param description description of the task
     * @param by due date or time as entered by the user
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns this task with its Deadline type marker and due text.
     *
     * @return formatted Deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by + ")";
    }
}
