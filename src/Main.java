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
            System.out.println(solution.objectiveFunction);
        }catch (ApplicationProblemException e){
            System.out.println("The method is not applicable!");
        }
    }
}