import Exceptions.DimensionsException;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface Vector extends Iterable<Double> {
    double get(int index) throws IndexOutOfBoundsException;

    void set(int index, double value) throws IndexOutOfBoundsException;

    int size();

    Vector multiply(double factor);

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

    default void scaleBy(double factor) {
        int n = size();
        for (int i = 0; i < n; i++) {
            set(i, get(i) * factor);
        }
    }

    default void mutateBy(Vector operand, BiFunction<Double, Double, Double> shader){
        int n = size();
        for (int i = 0; i < n; i++) {
            set(i, shader.apply(get(i), operand.get(i)));
        }
    }

    default boolean all(Predicate<Double> condition) {
        int n = size();
        for (int i = 0; i < n; i++) {
            if (!condition.test(get(i))) {
                return false;
            }
        }
        return true;
    }

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

    default int theMost(BiPredicate<Double, Double> criteria){
        return theMostIn(criteria, 0, size());
    }

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
