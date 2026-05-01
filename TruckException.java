/**
 * Custom exception for Truck-related errors
 */
public class TruckException extends RuntimeException {
    public TruckException(String message) {
        super(message);
    }

    public TruckException(String message, Throwable cause) {
        super(message, cause);
    }
}
