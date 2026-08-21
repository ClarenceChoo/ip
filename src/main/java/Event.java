/**
 * Represents a task that takes place between two dates or times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete Event with its start and end text.
     *
     * @param description description of the task
     * @param from start date or time as entered by the user
     * @param to end date or time as entered by the user
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this task with its Event type marker and time range.
     *
     * @return formatted Event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.from + " to: " + this.to + ")";
    }
}
