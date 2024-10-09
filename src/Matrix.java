import Exceptions.DimensionsException;
import jdk.jshell.spi.ExecutionControl;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Matrix {
    protected boolean isTransposed;
    protected int rows;
    protected int columns;
    protected double[] lineRepresentation;

    public static Matrix scan(Scanner stream) throws ExecutionControl.NotImplementedException {
        // TODO: This
        throw new ExecutionControl.NotImplementedException("blah-blah");
    }

    public static Matrix Identity(int size) {
        Matrix result = new Matrix(size, size);
        for (int i = 0; i < size; i++) {
            result.set(i, i, 1);
        }
        return result;
    }

    public Matrix combineRight(Matrix augmentation) {
        Matrix result = new Matrix(Math.max(rows, augmentation.rows), columns + augmentation.columns);
        try {
            result.absorb(this, 0, 0);
            result.absorb(augmentation, result.rows - augmentation.rows, columns);
        } catch (DimensionsException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Matrix combineRight(Vector augmentation) {
        Matrix result = new Matrix(Math.max(rows, augmentation.size()), columns + 1);
        try {
            result.absorb(this, 0, 0);
            result.absorb(augmentation, result.rows - augmentation.size(), columns, false);
        } catch (DimensionsException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Matrix(List<List<Double>> representation) throws DimensionsException {
        rows = representation.size();
        columns = representation.get(0).size();
        lineRepresentation = new double[rows * columns];
        int i = 0, j = 0;
        for (List<Double> row : representation) {
            if (row.size() != columns) {
                throw new DimensionsException("All rows of matrix should have same dimension");
            }
            i++;
            for (Double item : row) {
                lineRepresentation[i * columns + j++] = item;
            }
        }
    }

    public Matrix combineTop(Vector augmentation) {
        Matrix result = new Matrix(rows + 1, Math.max(columns, augmentation.size()));
        try {
            result.absorb(this, 1, 0);
            result.absorb(augmentation, 0, 0, true);
        } catch (DimensionsException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Matrix() {
        this.rows = 0;
        this.columns = 0;
        this.isTransposed = false;
        this.lineRepresentation = null;
    }

    public Matrix(int n, int m) {
        this.rows = n;
        this.columns = m;
        this.isTransposed = false;
        this.lineRepresentation = new double[n * m];
    }

    // !!! MUTABLE ENTRY COPY !!!
    public Matrix(Matrix origin) {
        this.rows = origin.rows;
        this.columns = origin.columns;
        this.isTransposed = origin.isTransposed;
        this.lineRepresentation = origin.lineRepresentation;
    }

    public Matrix clone() {
        Matrix clone = new Matrix(getRows(), getColumns());
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                clone.set(i, j, get(i, j));
            }
        }
        return clone;
    }

    public RowVector get(int index) {
        return new RowVector(this, index);
    }

    public Matrix subMatrix(int sr, int sc, int er, int ec) throws IndexOutOfBoundsException {
        Matrix result = new Matrix(er - sr, ec - sc);
        for (int i = sr; i < er; i++) {
            for (int j = sc; j < ec; j++) {
                result.get(i - sr).set(j - sc, this.get(i).get(j));
            }
        }
        return result;
    }

    public double get(int row, int col) throws IndexOutOfBoundsException {
        if (row >= getRows() || col >= getColumns()) {
            throw new IndexOutOfBoundsException("Index is not reachable");
        }
        return isTransposed ? lineRepresentation[col * rows + row] : lineRepresentation[row * columns + col];
    }

    public void set(int row, int col, double value) throws IndexOutOfBoundsException {
        if (row >= getRows() || col >= getColumns()) {
            throw new IndexOutOfBoundsException("Index is not reachable");
        }
        if (isTransposed) {
            lineRepresentation[col * rows + row] = value;
        } else {
            lineRepresentation[row * columns + col] = value;
        }
    }

    public int getRows() {
        return isTransposed ? columns : rows;
    }

    public int getColumns() {
        return isTransposed ? rows : columns;
    }

    public Matrix add(Matrix another) throws DimensionsException {
        if (getRows() != another.getRows() || getColumns() != another.getColumns()) {
            throw new DimensionsException("Error: the dimensional problem occurred in matrix summation");
        }

        Matrix result = new Matrix(getRows(), getColumns());

        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                result.set(i, j, this.get(i, j) + another.get(i, j));
            }
        }

        return result;
    }

    public Matrix multiply(int factor) throws DimensionsException {
        Matrix result = new Matrix(getRows(), getColumns());
        try {
            for (int i = 0; i < getRows(); i++) {
                for (int j = 0; j < getColumns(); j++) {
                    result.set(i, j, this.get(i, j) * factor);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new DimensionsException(e.getMessage());
        }

        return result;
    }

    public Matrix subtract(Matrix another) throws DimensionsException {
        return add(another.multiply(-1));
    }

    public Matrix multiply(Matrix another) throws DimensionsException {
        if (getColumns() != another.getRows()) {
            throw new DimensionsException("Error: the dimensional problem occurred in Matrix-Matrix multiplication");
        }

        Matrix result = new Matrix(getRows(), another.getColumns());
        try {
            for (int i = 0; i < getRows(); i++) {
                for (int j = 0; j < another.getColumns(); j++) {
                    result.set(i, j, get(i).multiply(another.get(j)));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new DimensionsException(e.getMessage());
        }

        return result;
    }

    // !!MUTABLE ENTRY COPY!! //
    public Matrix transposed() {
        Matrix clone = new Matrix(this);
        clone.isTransposed = !isTransposed;
        return clone;
    }

    private void absorb(Matrix reference, int startRow, int startCol) throws DimensionsException {
        try {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    set(startRow + i, startCol + j, reference.get(i, j));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new DimensionsException("Matrix" + rows + "x" + columns + " is not enough to absorb matrix "
                    + reference.rows + "x" + reference.columns + " into the ("
                    + startRow + ", " + startCol + ") position");
        }
    }

    private void absorb(Vector reference, int startRow, int startCol, boolean isAsRow) throws DimensionsException {
        int n = reference.size();
        try {
            for (int i = 0; i < n; i++) {
                set(
                        startRow + (isAsRow ? i : 0),
                        startCol + (isAsRow ? 0 : i),
                        reference.get(i)
                );
            }
        } catch (IndexOutOfBoundsException e) {
            throw new DimensionsException("Matrix" + rows + "x" + columns + " is not enough to absorb "
                    + (isAsRow ? "row" : "column") + " vector of size"
                    + reference.size() + " into the ("
                    + startRow + ", " + startCol + ") position");
        }
    }
}
