/**
 * Custom exception for Date-related errors
 */
public class DateException extends RuntimeException {
    public DateException(String message) {
        super(message);
    }

    public DateException(String message, Throwable cause) {
        super(message, cause);
    }
}
