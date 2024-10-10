package Exceptions;

// Exception for dimension mismatches in matrix or vector operations
public class DimensionsException extends MatrixException {
    public DimensionsException(String message) {
        super(message);
    }
}
