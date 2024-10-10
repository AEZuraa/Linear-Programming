import Exceptions.DimensionsException;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

// Interface for vectors, with various utility methods for vector operations
public interface Vector extends Iterable<Double> {
    // Get the value at a specific index
    double get(int index) throws IndexOutOfBoundsException;

    // Set the value at a specific index
    void set(int index, double value) throws IndexOutOfBoundsException;

    // Get the size of the vector
    int size();

    // Immutably multiply the vector by a scalar value
    Vector multiply(double factor);

    // Perform dot product multiplication with another vector
    default double multiply(Vector another) throws IndexOutOfBoundsException, DimensionsException {
        if (size() != another.size()) {
            throw new DimensionsException("Vectors of different size cannot be multiplied");
        }
        double dotProd = 0;
        for (int i = 0; i < size(); ++i) {
            dotProd += this.get(i) * another.get(i);
        }
        return dotProd;
    }

    // Mutably scale the vector by a factor
    default void scaleBy(double factor) {
        int n = size();
        for (int i = 0; i < n; i++) {
            set(i, get(i) * factor);
        }
    }

    // Mutably apply an operation to each element of the vector based on another vector
    default void mutateBy(Vector operand, BiFunction<Double, Double, Double> shader){
        int n = size();
        for (int i = 0; i < n; i++) {
            set(i, shader.apply(get(i), operand.get(i)));
        }
    }

    // Check if all elements of the vector satisfy a condition
    default boolean all(Predicate<Double> condition) {
        int n = size();
        for (int i = 0; i < n; i++) {
            if (!condition.test(get(i))) {
                return false;
            }
        }
        return true;
    }

    // Find the index of the element that stays at the top of linear order, formed by the given criteria within a range
    default int theMostIn(BiPredicate<Double, Double> criteria, int from, int to){
        int n = size();
        if (from>to || to>n){
            throw new IndexOutOfBoundsException("Vector with size " + n + " does not contain slice [" + from + ":" + to + "]");
        }
        int res = 0;
        for (int i = from; i < to; i++) {
            if (criteria.test(get(i), get(res))) {
                res = i;
            }
        }
        return res;
    }

    // Find the index of the element that stays at the top of linear order, formed by the given criteria
    default int theMost(BiPredicate<Double, Double> criteria){
        return theMostIn(criteria, 0, size());
    }

    @Override
    default Iterator<Double> iterator() {
        return new Iterator<>() {
            int current = 0;

            @Override
            public boolean hasNext() {
                return current < size();
            }

            @Override
            public Double next() {
                return get(current++);
            }
        };
    }
}
