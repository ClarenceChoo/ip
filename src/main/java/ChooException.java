/**
 * Represents an invalid command or argument entered into CHOO.
 */
public class ChooException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a CHOO-specific exception with a user-facing explanation.
     *
     * @param message explanation of the invalid input
     */
    public ChooException(String message) {
        super(message);
    }
}
