import java.util.Arrays;
import java.util.Scanner;

public class RowVector implements Vector {

    private final Matrix matrix;
    private final int index;

    public RowVector(Matrix matrix, int index) {
        this.matrix = matrix;
        this.index = index;
    }

    private RowVector(int n) {
        matrix = new Matrix(1, n);
        index = 0;
    }

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

