// Class representing a slice (subsection) of a vector
public class VectorSlice implements Vector {
    Vector vector; // Reference to the original vector
    int start; // Start index of the slice
    int stop; // End index of the slice

    // Constructor to create a slice from an existing vector
    public VectorSlice(Vector origin, int start, int stop) {
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
