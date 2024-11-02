import Exceptions.DimensionsException;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Interface for vectors, with various utility methods for vector operations
 */
public interface Vector extends Iterable<Double> {
    /**
     * Get the value at a specific index
     *
     * @param index position of item in a vector
     * @return value stored at the index
     * @throws IndexOutOfBoundsException if position is not defined
     */
    double get(int index) throws IndexOutOfBoundsException;

    /**
     * Set the value at a specific index
     *
     * @param index position of item in a vector
     * @param value new value, which will be assigned upon the position
     * @throws IndexOutOfBoundsException if position is not defined
     */
    void set(int index, double value) throws IndexOutOfBoundsException;

    /**
     * Get the size of the vector
     *
     * @return amount of items, stored inside the vector
     */
    int size();

    /**
     * Immutably multiply the vector by a scalar value
     *
     * @param factor number on which all items in vector will be multiplied
     * @return vector multiplied by factor
     */
    Vector multiply(double factor);

    /**
     * Perform dot product multiplication with another vector
     *
     * @param another second operand for the dot product
     * @return result of the dot product operation (x_1*y_1 + x_2*y_2 + ... + x_n*y_n)
     * @throws DimensionsException If vectors have not the same direction
     */
    default double multiply(Vector another) throws DimensionsException {
        if (size() != another.size()) {
            throw new DimensionsException("Vectors of different size cannot be multiplied");
        }
        double dotProd = 0;
        for (int i = 0; i < size(); ++i) {
            dotProd += this.get(i) * another.get(i);
        }
        return dotProd;
    }

    /**
     * TODO: this (doc)
     *
     * @param matrix
     * @return
     * @throws DimensionsException
     */
    default Vector multiply(Matrix matrix) throws DimensionsException {
        int n = size();
        Vector result = new ColumnVector(n);
        for (int i = 0; i < n; i++) {
            result.set(i, multiply(new ColumnVector(matrix, i)));
        }
        return result;
    }

    default Vector extend(int n) {
        Vector clone = new RowVector(this.size()+n);
        for (int i = 0; i < this.size()+n; i++) {
            if (i < this.size()) {
                clone.set(i, this.get(i));
            } else {
                clone.set(i, 0);
            }
        }
        return clone;
    }

    default Vector extendWith(Vector elems) {
        Vector clone = new RowVector(this.size()+elems.size());
        for (int i = 0; i < this.size()+elems.size(); i++) {
            if (i < this.size()) {
                clone.set(i, this.get(i));
            } else {
                clone.set(i, elems.get(i-this.size()));
            }
        }
        return clone;
    }

    /**
     * Mutably scale the vector by a factor
     *
     * @param factor number to be multiplied on each vector element
     */
    default void scaleBy(double factor) {
        int n = size();
        for (int i = 0; i < n; i++) {
            set(i, get(i) * factor);
        }
    }

    /**
     * Mutably apply an operation to each element of the vector based on another vector.
     * x_i = shader(x_i, y_i)
     *
     * @param operand y vector
     * @param shader  function to be applied
     */
    default void mutateBy(Vector operand, BiFunction<Double, Double, Double> shader) {
        int n = size();
        for (int i = 0; i < n; i++) {
            set(i, shader.apply(get(i), operand.get(i)));
        }
    }

    /**
     * TODO: doc
     * @param operand
     * @param shader
     * @return
     */
    Vector getMutated(Vector operand, BiFunction<Double, Double, Double> shader);

    /**
     * Check if all elements of the vector satisfy a condition
     *
     * @param condition condition
     * @return true if all elements satisfy the condition, false otherwise
     */
    default boolean all(Predicate<Double> condition) {
        int n = size();
        for (int i = 0; i < n; i++) {
            if (!condition.test(get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find the index of the element that stays at the top of linear order, formed by the given criteria
     *
     * @param criteria base predicate of a linear order
     * @return last element in linear order from vector
     */
    default int theMost(BiPredicate<Double, Double> criteria) {
        int n = size();
        int res = 0;
        for (int i = 0; i < n; i++) {
            if (criteria.test(get(i), get(res))) {
                res = i;
            }
        }
        return res;
    }

    default double cardinality() {
        double res = 0;
        int n = size();
        double item;
        for (int i = 0; i < n; i++) {
            item = get(i);
            res += item * item;
        }
        return Math.sqrt(res);
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
