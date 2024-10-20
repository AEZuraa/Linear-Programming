import Exceptions.DimensionsException;

import java.util.*;

/** Represents a matrix, providing methods to manipulate and perform operations on matrices */
public class Matrix {
    /** whether the matrix is transposed */
    protected boolean isTransposed;
    /** number of rows */
    protected int rows;
    /** number of columns */
    protected int columns;
    /** flat array representation of the matrix */
    protected double[] lineRepresentation;

    /**
     * Scans input to create a matrix from multiple rows of data.
     * Caret must point to start of the new line.
     * Each line of input = row in matrix. Space is separator between elements
     * Empty line is considered as end of the matrix
     *
     * @param stream input stream
     * @return matrix from input
     * @throws NumberFormatException  if input string cannot be considered as collection of doubles with space-separation
     * @throws NoSuchElementException if input is ends before matrix is scanned
     * @throws DimensionsException if rows consist from not equal amount of elements
     */
    public static Matrix scan(Scanner stream) throws DimensionsException {
        String line;
        ArrayList<double[]> outRepresentation = new ArrayList<>();
        while (!(line = stream.nextLine()).isEmpty()) {
            outRepresentation.add(
                    Arrays.stream(line.split(" "))
                            .mapToDouble(Double::parseDouble)
                            .toArray());
        }
        return new Matrix(outRepresentation);
    }

    /**
     * Generates an identity matrix of given size
     * @param size dimension of generating Identity
     * @return Matrix (size x size) with ones on diagonal and zeros in all another places
     */
    public static Matrix Identity(int size) {
        Matrix result = new Matrix(size, size);
        for (int i = 0; i < size; i++) {
            result.set(i, i, 1);
        }
        return result;
    }

    // Constructor to create a matrix with given dimensions (rows and columns)
    public Matrix(int n, int m) {
        this.rows = n;
        this.columns = m;
        this.isTransposed = false;
        this.lineRepresentation = new double[n * m];
    }

    // !!! MUTABLE ENTRY COPY !!!
    // Copy constructor to create a new matrix from an existing one
    public Matrix(Matrix origin) {
        this.rows = origin.rows;
        this.columns = origin.columns;
        this.isTransposed = origin.isTransposed;
        this.lineRepresentation = origin.lineRepresentation;
    }

    // Constructor for Matrix class, initializes matrix from a 2D list representation
    public Matrix(List<double[]> representation) throws DimensionsException {
        rows = representation.size();
        columns = representation.get(0).length;
        lineRepresentation = new double[rows * columns];
        int i = 0, j;
        for (double[] row : representation) {
            if (row.length != columns) {
                throw new DimensionsException("All rows of matrix should have same dimension");
            }
            j = 0;
            for (double item : row) {
                lineRepresentation[i * columns + j++] = item;
            }
            i++;
        }
    }

    // Combines the current matrix with another matrix on the right.
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

    // Combines the current matrix with a vector on the right
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

    // Combines the matrix with a vector on top (adds the vector as a row)
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

    // Get a row from the matrix as a RowVector object
    public RowVector get(int index) {
        return new RowVector(this, index);
    }

    // Get 2-dimensional matrix slice. Returns continuous part of a matrix within given region
    public Matrix subMatrix(int startRow, int startColumn, int endRow, int endColumn) throws IndexOutOfBoundsException {
        Matrix result = new Matrix(endRow - startRow, endColumn - startColumn);
        for (int i = startRow; i < endRow; i++) {
            for (int j = startColumn; j < endColumn; j++) {
                result.set(i - startRow, j - startColumn, this.get(i, j));
            }
        }
        return result;
    }

    // Get the element at the specified row and column
    public double get(int row, int col) throws IndexOutOfBoundsException {
        if (row >= getRows() || col >= getColumns()) {
            throw new IndexOutOfBoundsException("Index is not reachable");
        }
        return isTransposed ? lineRepresentation[col * rows + row] : lineRepresentation[row * columns + col];
    }

    // Set the element at the specified row and column
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

    // Get the number of rows in the matrix (considering transposition)
    public int getRows() {
        return isTransposed ? columns : rows;
    }

    // Get the number of columns in the matrix (considering transposition)
    public int getColumns() {
        return isTransposed ? rows : columns;
    }

    // Immutably multiply the matrix by a scalar factor
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

    // Immutably multiply this matrix by another matrix
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

    /**
     * TODO: this doc
     * @param one
     * @return
     * @throws DimensionsException
     */
    public Vector multiply(Vector one) throws DimensionsException {
        int n = getRows();
        Vector result = new ColumnVector(n);
        for (int i = 0; i < n; i++) {
            result.set(i, get(i).multiply(one));
        }
        return result;
    }

    public Matrix getPseudoInverse(){

    }

    // Returns the transposed representation of the matrix, with same entry
    // (changes over transposed one WILL affect original one)
    public Matrix transposed() {
        Matrix clone = new Matrix(this);
        clone.isTransposed = !isTransposed;
        return clone;
    }

    @Override
    public Matrix clone() {
        Matrix clone = new Matrix(getRows(), getColumns());
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                clone.set(i, j, get(i, j));
            }
        }
        return clone;
    }

    // Copies the contents of the reference matrix into the current matrix starting at the specified row and column
    private void absorb(Matrix reference, int startRow, int startCol) throws DimensionsException {
        try {
            for (int i = 0; i < reference.rows; i++) {
                for (int j = 0; j < reference.columns; j++) {
                    set(startRow + i, startCol + j, reference.get(i, j));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new DimensionsException("Matrix" + rows + "x" + columns + " is not enough to absorb matrix "
                    + reference.rows + "x" + reference.columns + " into the ("
                    + startRow + ", " + startCol + ") position");
        }
    }

    // Absorbs the contents of a vector (as a row or column) into the matrix at the specified start position
    private void absorb(Vector reference, int startRow, int startCol, boolean isAsRow) throws DimensionsException {
        int n = reference.size();
        try {
            for (int i = 0; i < n; i++) {
                set(
                        startRow + (isAsRow ? 0 : i),
                        startCol + (isAsRow ? i : 0),
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
