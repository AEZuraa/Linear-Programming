import Exceptions.ApplicationProblemException;
import jdk.jshell.spi.ExecutionControl;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ExecutionControl.NotImplementedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 0 for minimization or 1 for maximization");
        int flag = scanner.nextInt();
        while (flag != 0 && flag != 1) {
            System.out.println("Enter 0 for minimization or 1 for maximization");
            flag = scanner.nextInt();
        }
        Vector objectiveFunction = ColumnVector.scan(scanner);
        System.out.println("---\n");
        Matrix constrains = Matrix.scan(scanner);
        System.out.println("---\n");
        Vector rightHandSide = RowVector.scan(scanner);
        System.out.println("---\n");
        try {
            SimplexMatrix solution;
            if (flag == 0) {
                solution = new SimplexMatrix(objectiveFunction, constrains, rightHandSide, false);
            } else {
                solution = new SimplexMatrix(objectiveFunction, constrains, rightHandSide);
            }
            while (!solution.iteration()){}
            System.out.println("Values of variables in optimal solution:\n" + solution.getObjectiveFunction());
            System.out.println("Maximum value of the objective function:\n" + solution.getObjectiveFunctionValue());
        }catch (ApplicationProblemException e){
            System.out.println("The method is not applicable!");
            System.err.println(e.getMessage());
        }
    }
}