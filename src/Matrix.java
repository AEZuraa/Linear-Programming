import Exceptions.DimensionsException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

// Represents a matrix, providing methods to manipulate and perform operations on matrices
public class Matrix {
    // whether the matrix is transposed
    protected boolean isTransposed;
    // number of rows
    protected int rows;
    // number of columns
    protected int columns;
    // flat array representation of the matrix
    protected double[] lineRepresentation;

    /**
     * Scans input to create a matrix from multiple rows of data.
     * Each line of input = row in matrix. Space is separator between elements
     * Empty line is considered as end of the matrix
     * @param stream input stream
     * @return matrix from input
     * @throws NumberFormatException if input string cannot be considered as collection of doubles with space-separation
     * @throws NoSuchElementException if input is ends before matrix is scanned
     */
    public static Matrix scan(Scanner stream) {
        String first = stream.nextLine();
        if (!first.isEmpty()) {
            int cols = first.split(" ").length;
            Matrix result = new Matrix(1, cols);
            for (int i = 0; i < cols; i++) {
                result.set(0, i, Double.parseDouble(first.split(" ")[i]));
            }
            int row = 0;
            while (true) {
                String line = stream.nextLine();
                if (line.isEmpty()) {
                    break;
                }
                result.addRow();
                row++;
                for (int i = 0; i < cols; i++) {
                    result.set(row, i, Double.parseDouble(line.split(" ")[i]));
                }
            }
            return result;
        }
        return new Matrix(0,0);
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

    // Default constructor for creating an empty matrix
    public Matrix() {
        this.rows = 0;
        this.columns = 0;
        this.isTransposed = false;
        this.lineRepresentation = null;
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

    // Method to clone a matrix, clone changes will not affect original matrix
    public Matrix clone() {
        Matrix clone = new Matrix(getRows(), getColumns());
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                clone.set(i, j, get(i, j));
            }
        }
        return clone;
    }

    // Get a row from the matrix as a RowVector object
    public RowVector get(int index) {
        return new RowVector(this, index);
    }

    public Matrix subMatrix(int sr, int sc, int er, int ec) throws IndexOutOfBoundsException {
        Matrix result = new Matrix(er - sr, ec - sc);
        for (int i = sr; i < er; i++) {
            for (int j = sc; j < ec; j++) {
                result.set(i - sr,j - sc, this.get(i, j));
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

    // Add two matrices element-wise
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

    // Immutably subtract another matrix from this matrix
    public Matrix subtract(Matrix another) throws DimensionsException {
        return add(another.multiply(-1));
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

    // Returns the representation of the matrix, with same entry (changes over transposed one WILL affect original one)
    public Matrix transposed() {
        Matrix clone = new Matrix(this);
        clone.isTransposed = !isTransposed;
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

    // Adds an empty row to the matrix by increasing the number of rows and resizing the matrix entry
    private void addRow() {
        rows++;
        int newSize = rows * columns;
        double[] newLineRepresentation = new double[newSize];
        for (int i = 0; i < lineRepresentation.length; i++) {
            newLineRepresentation[i] = lineRepresentation[i];
        }
        lineRepresentation = newLineRepresentation;
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
