import java.util.function.BiFunction;

/** Class representing a slice (subsection) of a vector */
public class VectorSlice implements Vector {
    /** Reference to the original vector */
    Vector vector;
    /** Start index of the slice */
    int start;
    /** End index of the slice */
    int stop;

    /**
     * Constructor to create a slice from an existing vector. Right border is exclusive
     * @param origin vector to be sliced
     * @param start index which will be the first item of the slice
     * @param stop index after the last element of the slice (with respect to the origin)
     * @throws IndexOutOfBoundsException if origin vector cannot contains slice
     */
    public VectorSlice(Vector origin, int start, int stop) {
        if (start > stop || stop > origin.size()) {
            throw new IndexOutOfBoundsException("Vector with size " + origin.size()
                    + " does not contain slice [" + start + ":" + stop + ")");
        }
        vector = origin;
        this.start = start;
        this.stop = stop;
    }

    @Override
    public double get(int index) throws IndexOutOfBoundsException {
        if (index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return vector.get(index + start);
    }

    @Override
    public void set(int index, double value) throws IndexOutOfBoundsException {
        if (index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        vector.set(index + start, value);
    }

    @Override
    public int size() {
        return stop - start;
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
        RowVector res = new RowVector(size());
        for (int i = 0; i < size(); i++) {
            res.set(i, get(i));
        }
        res.mutateBy(operand, shader);
        return res;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        int n = size();
        for (int i = 0; i < n; i++) {
            sb.append(get(i)).append(' ');
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
