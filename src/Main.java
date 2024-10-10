import Exceptions.ApplicationProblemException;
import jdk.jshell.spi.ExecutionControl;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ExecutionControl.NotImplementedException {
        Scanner scanner = new Scanner(System.in);
        int isMaximization = -1;
        while (isMaximization != 0 && isMaximization != 1) {
            try {
                System.out.println("Enter 0 for minimization or 1 for maximization");
                isMaximization = Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                continue;
            }
        }
        Vector objectiveFunction = ColumnVector.scan(scanner);
        Matrix constrains = Matrix.scan(scanner);
        Vector rightHandSide = RowVector.scan(scanner);
        try {
            SimplexMatrix solution;
            solution = new SimplexMatrix(objectiveFunction, constrains, rightHandSide, isMaximization == 1);
            while (!solution.iteration(0.001)) {
            }
            System.out.println("Values of variables in optimal solution:\n" + solution.getObjectiveFunction());
            System.out.println("Maximum value of the objective function:\n" + solution.getObjectiveFunctionValue());
        } catch (ApplicationProblemException e) {
            System.out.println("The method is not applicable!");
            System.err.println(e.getMessage());
        }
    }
}