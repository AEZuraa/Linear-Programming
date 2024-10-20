import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.BiFunction;

/**
 * Represents a row vector, used for storing and manipulating row data
 */
public class RowVector implements Vector {
    /**
     * underlying matrix to hold vector data
     */
    private final Matrix matrix;
    /**
     * index of the row within the matrix
     */
    private final int index;

    /**
     * Constructor for referencing a row from a matrix.
     * Does not copy the items and mutable operations mutate the matrix entry
     *
     * @param matrix matrix, which row will be assigned
     * @param index  index of row in present matrix
     */
    public RowVector(Matrix matrix, int index) {
        this.matrix = matrix;
        this.index = index;
    }

    /**
     * Constructor for creating an empty RowVector of given size
     *
     * @param n size of the vector
     */
    public RowVector(int n) {
        matrix = new Matrix(1, n);
        index = 0;
    }

    /**
     * TODO: doc
     *
     * @param size
     * @return
     */
    public static RowVector one(int size, double value) {
        RowVector res = new RowVector(size);
        for (int i = 0; i < size; i++) {
            res.set(i, value);
        }
        return res;
    }

    /**
     * Scans input to create a vector from line of data.
     * Caret must point to start of the new line.
     * Space is separator between elements
     *
     * @param stream input stream
     * @return vector from input
     * @throws NumberFormatException  if input string cannot be considered as collection of doubles with space-separation
     * @throws NoSuchElementException if input is ends before matrix is scanned
     */
    public static RowVector scan(Scanner stream) {
        double[] items = Arrays.stream(stream.nextLine().split(" "))
                .mapToDouble(Double::parseDouble)
                .toArray();
        RowVector result = new RowVector(items.length);
        for (int i = 0; i < items.length; i++) {
            result.set(i, items[i]);
        }
        return result;
    }

    @Override
    public double get(int columnIndex) throws IndexOutOfBoundsException {
        return matrix.get(index, columnIndex);
    }

    @Override
    public void set(int columnIndex, double value) throws IndexOutOfBoundsException {
        matrix.set(index, columnIndex, value);
    }

    @Override
    public int size() {
        return matrix.isTransposed ? matrix.rows : matrix.columns;
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
    public Vector getMutated(Vector operand, BiFunction<Double, Double, Double> shader) {
        Vector res = clone();
        res.mutateBy(operand, shader);
        return res;
    }

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

    @Override
    public RowVector clone() {
        RowVector clone = new RowVector(size());
        for (int i = 0; i < size(); i++) {
            clone.set(i, get(i));
        }
        return clone;
    }
}

