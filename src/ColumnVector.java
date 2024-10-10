import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Represents a column vector, used for storing and manipulating column data */
public class ColumnVector implements Vector {
    /** underlying matrix to hold vector data */
    private final Matrix matrix;
    /** index of the column within the matrix */
    private final int index;

    /**
     * Scans input to create a vector from multiple rows of data.
     * Caret must point to start of the new line.
     * Each line of input = item in vector. Each line should consist from one number.
     * Empty line is considered as end of the vector
     * @param stream input stream
     * @return vector from input
     * @throws NumberFormatException if input stream cannot be considered as collection of doubles written in column
     * @throws NoSuchElementException if input is ends before matrix is scanned
     */
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

    /**
     * Constructor for creating an empty ColumnVector of given size
     * @param n size of the vector
     */
    ColumnVector(int n) {
        matrix = new Matrix(n, 1);
        index = 0;
    }

    /**
     * Constructor for referencing a column from a matrix.
     * Does not copy the items and mutable operations mutate the matrix entry
     * @param source matrix, which column will be assigned
     * @param column index of column in present matrix
     */
    ColumnVector(Matrix source, int column) {
        matrix = source;
        index = column;
    }

    @Override
    public double get(int index) throws IndexOutOfBoundsException {
        return matrix.get(index, this.index);
    }

    @Override
    public void set(int index, double value) throws IndexOutOfBoundsException {
        matrix.set(index, this.index, value);
    }

    @Override
    public Vector multiply(double factor) {
        ColumnVector result = new ColumnVector(size());
        int n = size();
        for (int i = 0; i < n; i++) {
            result.set(i, get(i) * factor);
        }
        return result;
    }

    @Override
    public int size() {
        return matrix.isTransposed ? matrix.columns : matrix.rows;
    }

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
