package Exceptions;

// Exception for improper conversions between matrix types
public class ImproperConversionException extends MatrixException {
    public ImproperConversionException(String message) {
        super(message);
    }
}
