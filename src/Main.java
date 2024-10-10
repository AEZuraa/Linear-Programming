import Exceptions.ApplicationProblemException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OptimizationMode mode;
        while (true) {
            try {
                System.out.println("Enter \"min\" for minimization either \"max\" for maximization");
                mode = OptimizationMode.valueOf(scanner.nextLine().trim().toUpperCase());
                break;
            } catch (IllegalArgumentException ignored) {
            }
        }
        System.out.println("Enter objective function coefficients (vector):");
        Vector objectiveFunction = RowVector.scan(scanner);
        System.out.println("Enter constrains functions coefficients (matrix):");
        Matrix constrains = Matrix.scan(scanner);
        System.out.println("Enter right hand sides for constrains (vector):");
        Vector rightHandSide = RowVector.scan(scanner);
        try {
            SimplexMatrix solution;
            solution = new SimplexMatrix(objectiveFunction, constrains, rightHandSide, 0.0001, mode);
            while (!solution.iteration()) {
                continue;
            }
            System.out.println(
                    "Values of variables in optimal solution:\n"
                            + solution.getObjectiveFunction()
            );
            System.out.println(
                    (mode.equals(OptimizationMode.MAX) ? "Maximum" : "Minimum")
                            + " value of the objective function:\n"
                            + solution.getObjectiveFunctionValue()
            );
        } catch (ApplicationProblemException e) {
            System.out.println("The method is not applicable!");
            System.err.println(e.getMessage());
        }
    }
}