import Exceptions.ApplicationProblemException;

import java.util.Comparator;
import java.util.Iterator;

/** Class representing a matrix used in the Simplex method for solving linear optimization problems */
public class SimplexMatrix {
    /** Matrix containing constraints, slack variables, and right-hand side */
    Matrix methodMatrix;
    /** Column vector for the right-hand side of constraints */
    Vector rightHandSide;
    /** Row vector for the objective function of the optimization problem */
    Vector objectiveFunction;
    /** Optimization mode for a problem */
    OptimizationMode mode;

    Comparator<Double> cmp;
    int[] basis;

    /**
     * Construct the simplex matrix
     * @param objectiveFunction Coefficients vector of linear function in R^(objectiveFunction.size())
     * @param constrains matrix of constrains over objectiveFunction variables
     *                   (constrain - inequality in form "x_1 + ... + x_n <= u_j")
     * @param rightHandSide vector of constrains right hand sides (u_1, u_2 ... u_m)
     * @param accuracy accuracy for double comparison. Influence (not always) on number of iteration,
     *                needed for find the optimal solution
     * @param mode either solution for the maximization or for the minimization problem
     * @throws ApplicationProblemException When simplex method is not applicable for the problem
     *                                     (have unbounded max/min value)
     */
    public SimplexMatrix(Vector objectiveFunction,
                         Matrix constrains,
                         Vector rightHandSide,
                         double accuracy,
                         OptimizationMode mode) throws ApplicationProblemException {
        if (!rightHandSide.all(item -> item >= 0)) {
            throw new ApplicationProblemException("Right hand side must be non negative for simplex method application");
        }
        methodMatrix = constrains
                .combineRight(Matrix.Identity(constrains.getRows()))
                .combineRight(rightHandSide)
                .combineTop(objectiveFunction.multiply(mode.factor));
        this.rightHandSide = new ColumnVector(methodMatrix, methodMatrix.getColumns() - 1);
        this.objectiveFunction = new VectorSlice(methodMatrix.get(0), 0, methodMatrix.getColumns());
        cmp = new DoublePreciseComparator(accuracy);
        this.mode = mode;
        basis = new int[constrains.getRows()];
        int componentsAmount = objectiveFunction.size();
        for (int i = 0; i < basis.length; i++) {
            basis[i] = componentsAmount + i;
        }
    }

    /**
     * Construct the simplex matrix for solving the maximization problem
     * @param objectiveFunction Coefficients vector of linear function in R^(objectiveFunction.size())
     * @param constrains matrix of constrains over objectiveFunction variables
     *                   (constrain - inequality in form "x_1 + ... + x_n <= u_j")
     * @param rightHandSide vector of constrains right hand sides (u_1, u_2 ... u_m)
     * @param accuracy accuracy for double comparison. Influence (not always) on number of iteration,
     *                needed for find the optimal solution
     * @throws ApplicationProblemException When simplex method is not applicable for the problem
     *                                     (have unbounded max value)
     */
    public SimplexMatrix(Vector objectiveFunction, Matrix constrains, Vector rightHandSide, double accuracy) throws ApplicationProblemException {
        this(objectiveFunction, constrains, rightHandSide, accuracy, OptimizationMode.MAX);
    }

    /**
     * Construct the simplex matrix for solving the maximization problem with absolute accuracy.
     * !! CAUTION: can produce infinite number of iterations !!
     * @param objectiveFunction Coefficients vector of linear function in R^(objectiveFunction.size())
     * @param constrains matrix of constrains over objectiveFunction variables
     *                   (constrain - inequality in form "x_1 + ... + x_n <= u_j")
     * @param rightHandSide vector of constrains right hand sides (u_1, u_2 ... u_m)
     * @throws ApplicationProblemException When simplex method is not applicable for the problem
     *                                     (have unbounded max value)
     */
    public SimplexMatrix(Vector objectiveFunction, Matrix constrains, Vector rightHandSide) throws ApplicationProblemException {
        this(objectiveFunction, constrains, rightHandSide, 0, OptimizationMode.MAX);
    }

    /**
     * Construct the simplex matrix with absolute accuracy.
     * !! CAUTION: can produce infinite number of iterations !!
     * @param objectiveFunction Coefficients vector of linear function in R^(objectiveFunction.size())
     * @param constrains matrix of constrains over objectiveFunction variables
     *                   (constrain - inequality in form "x_1 + ... + x_n <= u_j")
     * @param rightHandSide vector of constrains right hand sides (u_1, u_2 ... u_m)
     * @param mode either solution for the maximization or for the minimization problem
     * @throws ApplicationProblemException When simplex method is not applicable for the problem
     *                                     (have unbounded max/min value)
     */
    public SimplexMatrix(Vector objectiveFunction, Matrix constrains, Vector rightHandSide, OptimizationMode mode) throws ApplicationProblemException {
        this(objectiveFunction, constrains, rightHandSide, 0, mode);
    }

    /**
     * Performs the iterative solution of the Simplex algorithm.
     * !! Mutate the entry !!
     * @throws ApplicationProblemException if unbounded solution was identified (function have unbounded max/min value)
     */
    public void solve() throws ApplicationProblemException{
        while (!iteration()){continue;}
    }

    /**
     * Performs one iteration of the Simplex algorithm.
     * !! Mutate the entry !!
     * @return true if the optimal solution is found, false otherwise.
     * @throws ApplicationProblemException if unbounded solution was identified (function have unbounded max/min value)
     */
    protected boolean iteration() throws ApplicationProblemException {
        double[] ratios = new double[rightHandSide.size()];
        int enters = objectiveFunction.theMost((a, b) -> cmp.compare(a, b) < 0);
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

    /**
     * State of the solution
     * @return the objective function vector on current iteration (values of all variables, including the slack ones)
     */
    public Vector getObjectiveFunction() {
        Vector result = new RowVector(methodMatrix.getColumns() - 1);
        Iterator<Double> rhs = rightHandSide.iterator();
        rhs.next();
        for (int index : basis) {
            result.set(index, rhs.next());
        }
        return result;
    }

    /**
     * Value of the function
     * @return the value of the objective function on current iteration
     */
    public double getObjectiveFunctionValue() {
        return methodMatrix.get(0, methodMatrix.getColumns() - 1) * (mode.equals(OptimizationMode.MAX) ? 1 : -1);
    }
}
