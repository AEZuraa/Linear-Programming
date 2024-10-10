import Exceptions.ApplicationProblemException;

import java.util.Comparator;
import java.util.Iterator;

public class SimplexMatrix {
    Matrix methodMatrix;
    ColumnVector rightHandSide;
    RowVector objectiveFunction;
    OptimizationMode mode;

    Comparator<Double> cmp;
    int[] basis;

    public SimplexMatrix(Vector objectiveFunction, Matrix constrains, Vector rightHandSide, double accuracy, OptimizationMode mode) throws ApplicationProblemException {
        if (!rightHandSide.all(item -> item >= 0)) {
            throw new ApplicationProblemException("Right hand side must be non negative for simplex method application");
        }
        methodMatrix = constrains
                .combineRight(Matrix.Identity(constrains.getRows()))
                .combineRight(rightHandSide)
                .combineTop(mode.equals(OptimizationMode.MAX) ? objectiveFunction.multiply(-1) : objectiveFunction);
        this.rightHandSide = new ColumnVector(methodMatrix, methodMatrix.getColumns() - 1);
        this.objectiveFunction = methodMatrix.get(0);
        cmp = new DoublePreciseComparator(accuracy);
        this.mode = mode;
        basis = new int[constrains.getRows()];
        int componentsAmount = objectiveFunction.size();
        for (int i = 0; i < basis.length; i++) {
            basis[i] = componentsAmount + i;
        }
    }

    public SimplexMatrix(Vector objectiveFunction, Matrix constrains, Vector rightHandSide, double accuracy) throws ApplicationProblemException {
        this(objectiveFunction, constrains, rightHandSide, accuracy, OptimizationMode.MAX);
    }

    public SimplexMatrix(Vector objectiveFunction, Matrix constrains, Vector rightHandSide) throws ApplicationProblemException {
        this(objectiveFunction, constrains, rightHandSide, 0, OptimizationMode.MAX);
    }

    public SimplexMatrix(Vector objectiveFunction, Matrix constrains, Vector rightHandSide, OptimizationMode mode) throws ApplicationProblemException {
        this(objectiveFunction, constrains, rightHandSide, 0, mode);
    }

    public boolean iteration() throws ApplicationProblemException {
        double[] ratios = new double[rightHandSide.size()];
        int enters = objectiveFunction.theMostIn(
                (a, b) -> cmp.compare(a, b) < 0,
                0,
                objectiveFunction.size() - 1);
        if (cmp.compare(objectiveFunction.get(enters), 0d) >= 0) {
            return true;
        }
        ColumnVector pivotColumn = new ColumnVector(methodMatrix, enters);
        int leaves = 1;
        // do not consider ratio z/z()
        for (int i = 1; i < methodMatrix.rows; i++) {
            ratios[i] = rightHandSide.get(i) / pivotColumn.get(i);
            if ((cmp.compare(ratios[i], ratios[leaves]) < 0 || cmp.compare(ratios[leaves], 0d) <= 0)
                    && cmp.compare(ratios[i], 0d) > 0) {
                leaves = i;
            }
        }
        RowVector pivotRow = methodMatrix.get(leaves);
        if (cmp.compare(ratios[leaves], 0d) <= 0) {
            throw new ApplicationProblemException("Unbounded solution");
        }
        pivotRow.scaleBy(1 / methodMatrix.get(leaves, enters));
        for (int i = 0; i < methodMatrix.rows; i++) {
            if (i == leaves) {
                continue;
            }
            double pivotColumnElement = pivotColumn.get(i);
            methodMatrix.get(i).mutateBy(pivotRow, (current, pivot) -> current - pivot * pivotColumnElement);
        }
        basis[leaves - 1] = enters;

        return false;
    }

    public Vector getObjectiveFunction() {
        Vector result = new RowVector(methodMatrix.getColumns() - 1);
        Iterator<Double> rhs = rightHandSide.iterator();
        rhs.next();
        for (int index : basis) {
            result.set(index, rhs.next());
        }
        return result;
    }

    public double getObjectiveFunctionValue() {
        return methodMatrix.get(0, methodMatrix.getColumns() - 1) * (mode.equals(OptimizationMode.MAX) ? 1 : -1);
    }
}
