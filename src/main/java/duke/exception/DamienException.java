package duke.exception;

/**
 * Represents an error caused by an invalid Damien command or task input.
 */
public class DamienException extends Exception {
    /** Keeps serialized exception instances compatible across application versions. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a message suitable for showing to the user.
     *
     * @param message the explanation of the invalid input
     */
    public DamienException(String message) {
        super(message);
    }
}
