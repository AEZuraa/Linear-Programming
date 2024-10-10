package Exceptions;

// Exception for errors in applying the Simplex method
public class ApplicationProblemException extends MatrixException {
    public ApplicationProblemException(String message) {
        super(message);
    }
}
