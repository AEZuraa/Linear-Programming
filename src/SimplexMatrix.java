import Exceptions.ApplicationProblemException;

import java.util.Comparator;

public class SimplexMatrix {
    Matrix methodMatrix;
    ColumnVector rightHandSide;
    RowVector objectiveFunction;

    public SimplexMatrix(Vector objectiveFunction, Matrix constrains, Vector rightHandSide, boolean isMax) throws ApplicationProblemException {
        if (!rightHandSide.all(item -> item >= 0)) {
            throw new ApplicationProblemException("Right hand side must be non negative for simplex method application");
        }
        methodMatrix = constrains
                .combineRight(Matrix.Identity(constrains.rows))
                .combineRight(rightHandSide)
                .combineTop(isMax ? objectiveFunction.multiply(-1) : objectiveFunction);
        this.rightHandSide = new ColumnVector(methodMatrix, methodMatrix.columns - 1);
        this.objectiveFunction = methodMatrix.get(0);
    }

    public SimplexMatrix(Vector objectiveFunction, Matrix constrains, Vector rightHandSide) throws ApplicationProblemException {
        this(objectiveFunction, constrains, rightHandSide, true);
    }

    public boolean iteration(double accuracy) throws ApplicationProblemException {
        double[] ratios = new double[rightHandSide.size()];
        Comparator<Double> cmp = new DoublePreciseComparator(accuracy);
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
            if (cmp.compare(ratios[i], ratios[leaves]) < 0 && cmp.compare(ratios[i], 0d) > 0) {
                leaves = i;
            }
        }
        RowVector pivotRow = methodMatrix.get(leaves);
        if (leaves == 1 && cmp.compare(ratios[leaves], 0d) <= 0) {
            throw new ApplicationProblemException("Unbounded solution");
        }
        pivotRow.scaleBy(1 / methodMatrix.get(leaves, enters));
        for (int i = 0; i < methodMatrix.rows; i++) {
            double pivotColumnElement = pivotColumn.get(i);
            methodMatrix.get(i).mutateBy(pivotRow, (current, pivot) -> current - pivot * pivotColumnElement);
        }

        return false;
    }

    public VectorSlice getObjectiveFunction() {
        return new VectorSlice(methodMatrix.get(0), 0, methodMatrix.getColumns() - 1);
    }

    public double getObjectiveFunctionValue() {
        return methodMatrix.get(0, methodMatrix.getColumns() - 1);
    }
}
