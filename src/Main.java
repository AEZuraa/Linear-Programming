import Exceptions.ApplicationProblemException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isMaximization;
        while (true) {
            try {
                System.out.println("Enter 0 for minimization or 1 for maximization");
                isMaximization = (1 == Integer.parseInt(scanner.next()));
                break;
            } catch (NumberFormatException ignored) {}
        }
        Vector objectiveFunction = ColumnVector.scan(scanner);
        Matrix constrains = Matrix.scan(scanner);
        Vector rightHandSide = RowVector.scan(scanner);
        try {
            SimplexMatrix solution;
            solution = new SimplexMatrix(objectiveFunction, constrains, rightHandSide, isMaximization);
            while (!solution.iteration(0.001)) {
                continue;
            }
            System.out.println("Values of variables in optimal solution:\n" + solution.getObjectiveFunction());
            System.out.println("Maximum value of the objective function:\n" + solution.getObjectiveFunctionValue());
        } catch (ApplicationProblemException e) {
            System.out.println("The method is not applicable!");
            System.err.println(e.getMessage());
        }
    }
}