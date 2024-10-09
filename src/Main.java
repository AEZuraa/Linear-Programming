import Exceptions.ApplicationProblemException;
import jdk.jshell.spi.ExecutionControl;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ExecutionControl.NotImplementedException {
        Scanner scanner = new Scanner(System.in);
        Vector objectiveFunction = ColumnVector.scan(scanner);
        Matrix constrains = Matrix.scan(scanner);
        Vector rightHandSide = RowVector.scan(scanner);
        try {
            SimplexMatrix solution = new SimplexMatrix(objectiveFunction, constrains, rightHandSide);
            while (solution.iteration()){}
            System.out.println("Values of variables in optimal solution:\n" + solution.getObjectiveFunction());
            System.out.println("Maximum value of the objective function:\n" + solution.getObjectiveFunctionValue());
        }catch (ApplicationProblemException e){
            System.out.println("The method is not applicable!");
        }
    }
}