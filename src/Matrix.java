import Exceptions.DimensionsException;
import Exceptions.ImproperConversionException;
import Exceptions.MatrixException;
import Exceptions.SingularityException;

import java.util.*;

/**
 * Represents a matrix, providing methods to manipulate and perform operations on matrices
 */
public class Matrix {
    /**
     * whether the matrix is transposed
     */
    protected boolean isTransposed;
    /**
     * number of rows
     */
    protected int rows;
    /**
     * number of columns
     */
    protected int columns;
    /**
     * flat array representation of the matrix
     */
    protected double[] lineRepresentation;

    private DoublePreciseComparator CMP;

    private static final DoublePreciseComparator DEFAULT_CMP = new DoublePreciseComparator(0.001);

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
     * @throws DimensionsException    if rows consist from not equal amount of elements
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
     * TODO: this doc
     *
     * @param diagonal
     * @return
     */
    public static Matrix diagonal(Vector diagonal) {
        Matrix res = new Matrix(diagonal.size(), diagonal.size());
        for (int i = 0; i < res.rows; i++) {
            res.set(i, i, diagonal.get(i));
        }
        return res;
    }

    /**
     * Generates an identity matrix of given size
     *
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
        this(n, m, false);
        this.lineRepresentation = new double[n * m];
    }

    public Matrix(DoublePreciseComparator cmp, int n, int m) {
        this(n, m);
        CMP = cmp;
    }

    // !!! MUTABLE ENTRY COPY !!!
    // Copy constructor to create a new matrix from an existing one
    public Matrix(Matrix origin) {
        this(origin.rows, origin.columns, origin.isTransposed);
        this.lineRepresentation = origin.lineRepresentation;
        this.CMP = origin.CMP;
    }

    // Constructor for Matrix class, initializes matrix from a 2D list representation
    public Matrix(List<double[]> representation) throws DimensionsException {
        this(representation.size(), representation.get(0).length, false);
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

    private Matrix(int n, int m, boolean isTransposed) {
        CMP = DEFAULT_CMP;
        this.isTransposed = isTransposed;
        rows = n;
        columns = m;
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

    // Subtract another matrix from this matrix
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

    /**
     * TODO: this doc
     *
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

    public Matrix getSubmatrix(int startRow, int startColumn, int endRow, int endColumn) {
        if (startRow < 0
                || startColumn < 0
                || endRow < 0
                || endColumn < 0
                || startRow >= getRows()
                || endRow >= getRows()
                || startColumn >= getColumns()
                || endColumn >= getColumns()
                || startRow > endRow
                || startColumn > endColumn
        ) {
            throw new IndexOutOfBoundsException("Improper submatrix boundasaries. Slice ("
                    + startRow + ", " + startColumn + "):(" + endRow + ", " + endColumn
                    + ") does not belong to matrix (" + getRows() + " x " + getColumns() + ")");
        }
        Matrix result = new Matrix(endRow - startRow, endColumn - startColumn);
        for (int i = 0; i < result.rows; i++) {
            for (int j = 0; j < result.columns; j++) {
                result.set(i, j, get(startRow + i, startColumn + j));
            }
        }
        return result;
    }

    /**
     * TODO: doc
     *
     * @return
     * @throws MatrixException
     */
    public Matrix getInverse(double accuracy) throws ImproperConversionException, SingularityException {
        if (columns != rows) {
            throw new ImproperConversionException("Only square matrices can have the inverse");
        }
        Matrix augmented = combineRight(Identity(rows));
        if (accuracy != DEFAULT_CMP.accuracy)
            augmented.CMP = new DoublePreciseComparator(accuracy);
        augmented.toRREF();
        return augmented.getSubmatrix(0, rows, rows, rows * 2);
    }

    public Matrix getInverse() throws ImproperConversionException, SingularityException {
        return getInverse(DEFAULT_CMP.accuracy);
    }

    public Matrix getPseudoInverse(double accuracy) throws SingularityException {
        try {
            return transposed().multiply(multiply(transposed()).getInverse(accuracy)).multiply(this);
        } catch (SingularityException e) {
            throw new SingularityException("Matrix have no pseudo inverse");
        } catch (MatrixException ignored) {
            throw new RuntimeException("Something went wrong... Pseudo inverse cannot be obtained");
        }
    }

    public Matrix getPseudoInverse() throws SingularityException {
        return getPseudoInverse(DEFAULT_CMP.accuracy);
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

    private void toRREF() throws SingularityException {
        int _rows = getRows();
        int _columns = getColumns();

        // TODO: refactor this piece of smelled and smelted code

        // eliminate forward
        boolean isNegated = false;
        for (int i = 0; i < _rows; ++i) {
            int curPermutation = getPivoting(i);
            if (i != curPermutation) {
                permute(i, curPermutation);
                isNegated = !isNegated;
            }
            if (CMP.compare(Math.abs(get(i, i)), 0d) < 0) {
                throw new SingularityException("Error: matrix A is singular");
            }
            for (int j = i + 1; j < _rows; ++j) {
                if (CMP.compare(Math.abs(get(j, i)), 0d) < 0) {
                    continue;
                }
                eliminate(j, i);
                isNegated = !isNegated;
            }
        }

        double diagonalProduct = 1;
        for (int i = 0; i < Math.min(columns, rows); i++) {
            diagonalProduct *= get(i, i);
        }
        if (CMP.compare(Math.abs(diagonalProduct), 0d) < 0) {
            throw new SingularityException("Error: matrix A is singular");
        }
        // eliminate backward
        for (int i = _rows - 1; i >= 0; --i) {
            for (int j = i - 1; j >= 0; --j) {
                if (CMP.compare(Math.abs(get(j, i)), 0d) >= 0) {
                    eliminate(j, i);
                }
            }
        }

        // diagonal normalize
        for (int i = 0; i < _rows; ++i) {
            double pivot = get(i, i);
            for (int j = 0; j < _columns; ++j) {
                set(i, j, get(i, j) / pivot);
            }
            set(i, i, 1d);
        }
    }

    private int getPivoting(int row) {
        int pivot = row;
        double maxValue = Math.abs(get(row, row));
        for (int i = row + 1; i < getRows(); ++i) {
            if (maxValue < Math.abs(get(i, row))) {
                maxValue = Math.abs(get(i, row));
                pivot = i;
            }
        }
        return pivot;
    }

    private void permute(int row1, int row2) {
        int col = getColumns();
        double temp = 0;
        for (int i = 0; i < col; i++) {
            temp = get(row1, i);
            set(row1, i, get(row2, i));
            set(row2, i, temp);
        }
    }

    private void eliminate(int row, int column) {
        double closureFactor = get(row, column) / get(column, column);
        new RowVector(this, row)
                .mutateBy(
                        new RowVector(this, column),
                        (cur, piv) -> cur - closureFactor * piv
                );
    }
}
