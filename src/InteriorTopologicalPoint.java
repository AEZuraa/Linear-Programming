import Exceptions.ApplicationProblemException;
import Exceptions.DimensionsException;
import Exceptions.SingularityException;

import java.util.Scanner;

public class InteriorTopologicalPoint {
    Vector objectiveFunction;
    Matrix constrains;
    Vector currentPoint;
    double alpha;
    DoublePreciseComparator cmp;
    OptimizationMode mode;

    public InteriorTopologicalPoint(
            Vector objectiveFunction,
            Matrix constraints,
            Vector rightHandSide,
            Vector initialPoint,
            double alpha,
            double accuracy,
            OptimizationMode mode
    ) throws DimensionsException, ApplicationProblemException {
        cmp = new DoublePreciseComparator(accuracy);
        Vector initialLHS;
        try {
            initialLHS = constraints.multiply(initialPoint);
        } catch (DimensionsException e) {
            throw new DimensionsException("Right hand side vector must have size of matrix columns amount");
        }
        initialLHS.mutateBy(rightHandSide, (a, b) -> b - a);
        if (!initialLHS.all((a) -> cmp.compare(a, 0d) >= 0)) {
            throw new ApplicationProblemException("Interior point must be inside the topological region");
        }
        this.objectiveFunction = objectiveFunction.multiply(mode.factor*-1);
        this.constrains = constraints.combineRight(Matrix.Identity(constraints.rows));
        this.alpha = alpha;
        this.currentPoint = initialPoint.extendWith(initialLHS).multiply(mode.factor*-1);
        this.mode = mode;
    }

    public Vector solve() throws DimensionsException, SingularityException {
        while (!iteration()) {
        }
        return new VectorSlice(currentPoint.multiply(mode.factor*(-1)), 0, currentPoint.size() - constrains.getRows());
    }

    // TODO: doc
    protected boolean iteration() throws DimensionsException, SingularityException {
        Matrix D = Matrix.diagonal(currentPoint);
        Matrix ATilda;
        try {
            ATilda = constrains.multiply(D);
        } catch (DimensionsException e) {
            throw new DimensionsException("The amount of columns in constraints matrix must be equal to the amount of rows in diagonal solution matrix");
        }
        Vector cTilda;
        try {
            cTilda = D.multiply(objectiveFunction);
        } catch (DimensionsException e) {
            throw new DimensionsException("The amount of columns in diagonal solution matrix must be equal to the amount of coefficients in objective function vector");
        }
        Matrix hui = ATilda.getPseudoInverse(cmp.accuracy);
        Matrix P = Matrix.Identity(ATilda.getColumns()).subtract(hui);
        Vector cp = P.multiply(cTilda);
        double factor = alpha / Math.abs(cp.get(cp.theMost((a, b) -> a < b)));
        Vector xTilda = RowVector.one(currentPoint.size(), 1);
        xTilda.mutateBy(cp, (one, value) -> one + factor * value);
        Vector xStar = D.multiply(xTilda);
        if (cmp.compare(xStar.getMutated(currentPoint, (a, b) -> a - b).cardinality(), 0d) <= 0) {
            currentPoint = xStar;
            return true;
        }
        currentPoint = xStar;
        return false;
    }


    /**
     * Solution for 2nd homework
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OptimizationMode mode;

        // Read optimization mode (min or max)
        while (true) {
            try {
                System.out.println("Enter \"min\" for minimization or \"max\" for maximization");
                mode = OptimizationMode.valueOf(scanner.nextLine().trim().toUpperCase());
                break;
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Read objective function coefficients
        System.out.println("Enter objective function coefficients (vector):");
        Vector objectiveFunction = RowVector.scan(scanner);

        // Read constraints matrix
        System.out.println("Enter constraint functions coefficients (matrix):");
        Matrix constraints;
        try {
            constraints = Matrix.scan(scanner);
        } catch (DimensionsException ignored) {
            throw new RuntimeException("Improper input, constraints is not a proper matrix");
        }

        // Read right-hand side values
        System.out.println("Enter right-hand sides for constraints (vector):");
        Vector rightHandSide = RowVector.scan(scanner);

        // Read initial starting point
        System.out.println("Enter initial point (vector):");
        Vector initialPoint = RowVector.scan(scanner);

        // Read accuracy
        System.out.println("Enter approximation accuracy (ε):");
        double accuracy = Double.parseDouble(scanner.nextLine());

        // Read alpha value for the Interior Point method
        double[] alphas = {0.5, 0.9};

        // Solve using Simplex method
        try {
            // Simplex method solution
            SimplexMatrix simplexSolution = new SimplexMatrix(objectiveFunction, constraints, rightHandSide, accuracy, mode);
            simplexSolution.solve();
            System.out.println(
                    (mode.equals(OptimizationMode.MAX) ? "Maximum" : "Minimum")
                            + " value of the objective function (Simplex):\n"
                            + simplexSolution.getObjectiveFunctionValue()
                            + "\nAt the point:\n"
                            + new VectorSlice(simplexSolution.getObjectiveFunction(), 0, objectiveFunction.size())
            );
        } catch (ApplicationProblemException e) {
            System.out.println("The method is not applicable!");
        }

        // Solve using Interior Point method for alpha = 0.5 & alpha = 0.9
        for (double alpha : alphas) {
            try {
                InteriorTopologicalPoint interiorPointSolver1 = new InteriorTopologicalPoint(objectiveFunction, constraints, rightHandSide, initialPoint, alpha, accuracy, mode);
                Vector solution1 = interiorPointSolver1.solve();
                double objectiveValue1 = objectiveFunction.multiply(solution1);
                System.out.println(
                        (mode.equals(OptimizationMode.MAX) ? "Maximum" : "Minimum")
                                + " value of the objective function (Interior Point, α=" + alpha + "):\n"
                                + objectiveValue1
                                + "\nAt the point:\n"
                                + new VectorSlice(solution1.multiply(mode.factor*(-1)), 0, solution1.size() - constraints.getRows())
                );
            } catch (ApplicationProblemException e) {
                System.out.println("The method is not applicable!");
                return;
            } catch (DimensionsException | SingularityException e) {
                System.out.println("An error occurred during Interior Point calculation: " + e.getMessage());
            }
        }
    }
}
