/**
 * Custom exception for Package-related errors
 */
public class PackageException extends RuntimeException {
    public PackageException(String message) {
        super(message);
    }

    public PackageException(String message, Throwable cause) {
        super(message, cause);
    }
}
