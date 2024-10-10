import java.util.Arrays;
import java.util.Scanner;

// Represents a row vector, used for storing and manipulating row data
public class RowVector implements Vector {

    private final Matrix matrix; // underlying matrix to hold vector data
    private final int index; // index of the row within the matrix

    // Constructor for extracting a row from a matrix
    public RowVector(Matrix matrix, int index) {
        this.matrix = matrix;
        this.index = index;
    }

    // Constructor for creating an empty RowVector of size n
    public RowVector(int n) {
        matrix = new Matrix(1, n);
        index = 0;
    }

    // Method to scan input and create a RowVector
    static RowVector scan(Scanner stream) {
        double[] items = Arrays.stream(stream.nextLine().split(" "))
                .mapToDouble(Double::parseDouble)
                .toArray();
        RowVector result = new RowVector(items.length);
        for (int i = 0; i < items.length; i++) {
            result.set(i, items[i]);
        }
        return result;
    }

    // Get value at specific column index in the row
    @Override
    public double get(int columnIndex) throws IndexOutOfBoundsException {
        return matrix.get(index, columnIndex);
    }

    // Set value at specific column index in the row
    @Override
    public void set(int columnIndex, double value) throws IndexOutOfBoundsException {
        matrix.set(index, columnIndex, value);
    }

    // Return the size of the row vector
    @Override
    public int size() {
        return matrix.isTransposed ? matrix.rows : matrix.columns;
    }

    // Multiply the vector by a scalar and return a new vector
    @Override
    public Vector multiply(double factor) {
        ColumnVector result = new ColumnVector(size());
        int n = size();
        for (int i = 0; i < n; i++) {
            result.set(i, get(i) * factor);
        }
        return result;
    }

    // Convert row vector to string
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int n = size();
        for (int i = 0; i < n; i++) {
            sb.append(get(i)).append(' ');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}

