import jdk.jshell.spi.ExecutionControl;

import java.util.Scanner;

public class ColumnVector implements Vector{
    private final Matrix matrix;
    private final int index;

    public static ColumnVector scan(Scanner stream) throws ExecutionControl.NotImplementedException {
        // TODO: This
        throw new ExecutionControl.NotImplementedException("blah-blah");
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
    public void set(int columnIndex, double value) throws IndexOutOfBoundsException {
        matrix.set(index, columnIndex, value);
    }

    @Override
    public Vector multiply(double factor) {
        ColumnVector result = new ColumnVector(size());
        int n = size();
        for (int i = 0; i < n; i++) {
            result.set(i, get(i)*factor);
        }
        return result;
    }

    @Override
    public int size() {
        return matrix.isTransposed ? matrix.rows : matrix.columns;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        int n = size();
        for (int i = 0; i < n; i++) {
            sb.append(get(i)).append('\n');
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
