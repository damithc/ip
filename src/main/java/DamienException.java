/**
 * Represents an error caused by an invalid Damien command or task input.
 */
public class DamienException extends Exception {
    /**
     * Creates an exception with a message suitable for showing to the user.
     *
     * @param message the explanation of the invalid input
     */
    public DamienException(String message) {
        super(message);
    }
}
