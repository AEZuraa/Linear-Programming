import Exceptions.ApplicationProblemException;
import Exceptions.DimensionsException;
import Exceptions.ImproperConversionException;
import Exceptions.SingularityException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read supply value vector
        System.out.println("Enter supply values (vector):");
        RowVector supply = RowVector.scan(scanner);

        // Read demand value vector
        System.out.println("Enter supply values (vector):");
        RowVector demand = RowVector.scan(scanner);

        // Read constraints matrix
        System.out.println("Enter constraint functions coefficients (matrix):");
        Matrix costs;
        try {
            costs = Matrix.scan(scanner);
        } catch (DimensionsException ignored) {
            throw new RuntimeException("Improper input, costs is not a proper matrix");
        }

        Chooser[] methods = {
                new VogelApproximation(),

        };

        // Solve using Interior Point method for alpha = 0.5 & alpha = 0.9
        for (Chooser method : methods) {
            try {
                TransportationModel solver = new TransportationModel(costs.clone(), demand.clone(), supply.clone(), method);
                double optimalValue = solver.solve();
                System.out.println("The result of the " + method + " is " + optimalValue);
//            } catch (ApplicationProblemException e) {
//                System.out.println("The method is not applicable!");
//                return;
//            } catch (DimensionsException | SingularityException e) {
//                System.out.println("An error occurred during Interior Point calculation: " + e.getMessage());
            } catch (ImproperConversionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
