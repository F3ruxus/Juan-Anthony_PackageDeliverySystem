/**
 * Custom exception for SpecialPackage-related errors
 */
public class SpecialPackageException extends PackageException {
    public SpecialPackageException(String message) {
        super(message);
    }

    public SpecialPackageException(String message, Throwable cause) {
        super(message, cause);
    }
}
