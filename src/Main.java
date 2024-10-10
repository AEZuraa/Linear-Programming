import Exceptions.ApplicationProblemException;

import java.sql.SQLOutput;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isMaximization;
        while (true) {
            try {
                System.out.println("Enter 0 for minimization or any another number for maximization");
                isMaximization = (0 != Integer.parseInt(scanner.nextLine()));
                break;
            } catch (NumberFormatException ignored) {}
        }
        System.out.println("Enter objective function coefficients (vector):");
        Vector objectiveFunction = RowVector.scan(scanner);
        System.out.println("Enter constrains functions coefficients (matrix):");
        Matrix constrains = Matrix.scan(scanner);
        System.out.println("Enter right hand sides for constrains (vector):");
        Vector rightHandSide = RowVector.scan(scanner);
        try {
            SimplexMatrix solution;
            solution = new SimplexMatrix(objectiveFunction, constrains, rightHandSide, 0.0001, isMaximization);
            while (!solution.iteration()) {
                continue;
            }
            System.out.println(
                    "Values of variables in optimal solution:\n"
                            + solution.getObjectiveFunction()
            );
            System.out.println(
                    (isMaximization? "Maximum": "Minimum")
                    + " value of the objective function:\n"
                    + solution.getObjectiveFunctionValue()
            );
        } catch (ApplicationProblemException e) {
            System.out.println("The method is not applicable!");
            System.err.println(e.getMessage());
        }
    }
}