import java.util.ArrayList;
import java.util.Scanner;

// Represents a column vector, used for storing and manipulating column data
public class ColumnVector implements Vector {
    private final Matrix matrix; // underlying matrix to hold vector data
    private final int index; // index of the column within the matrix

    // Method to scan input from user and create a ColumnVector
    public static ColumnVector scan(Scanner stream) {
        ArrayList<Double> input = new ArrayList<>();
        String line;
        while (!(line = stream.nextLine()).isEmpty()) {
            input.add(Double.parseDouble(line));
        }
        int n = input.size();
        ColumnVector result = new ColumnVector(n);
        for (int i = 0; i < n; i++) {
            result.set(i, input.get(i));
        }
        return result;
    }

    // Constructor for creating an empty ColumnVector of size n
    ColumnVector(int n) {
        matrix = new Matrix(n, 1);
        index = 0;
    }

    // Constructor for referencing a column from an existing matrix
    ColumnVector(Matrix source, int column) {
        matrix = source;
        index = column;
    }

    // Get value at specific index in the column
    @Override
    public double get(int index) throws IndexOutOfBoundsException {
        return matrix.get(index, this.index);
    }

    // Set value at specific index in the column
    @Override
    public void set(int index, double value) throws IndexOutOfBoundsException {
        matrix.set(index, this.index, value);
    }

    // Multiply vector by a scalar factor and return a new vector
    @Override
    public Vector multiply(double factor) {
        ColumnVector result = new ColumnVector(size());
        int n = size();
        for (int i = 0; i < n; i++) {
            result.set(i, get(i) * factor);
        }
        return result;
    }

    // Return the size of the vector
    @Override
    public int size() {
        return matrix.isTransposed ? matrix.columns : matrix.rows;
    }

    // Convert vector to a string representation
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int n = size();
        for (int i = 0; i < n; i++) {
            sb.append(get(i)).append('\n');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
