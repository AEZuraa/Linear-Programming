import java.util.ArrayList;
import java.util.Scanner;

public class ColumnVector implements Vector {
    private final Matrix matrix;
    private final int index;


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

    ColumnVector(int n) {
        matrix = new Matrix(n, 1);
        index = 0;
    }

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
