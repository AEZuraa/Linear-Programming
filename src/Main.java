import Exceptions.ApplicationProblemException;
import Exceptions.DimensionsException;
import Exceptions.SingularityException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws DimensionsException {
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
// bb
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
                double objectiveValue1 = objectiveFunction.extend(constraints.rows).multiply(solution1);
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
