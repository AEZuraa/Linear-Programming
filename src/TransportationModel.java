import Exceptions.DimensionsException;
import Exceptions.ImproperConversionException;

import java.util.ArrayList;
import java.util.Scanner;

public class TransportationModel {
    protected Matrix costs;
    protected Vector demand;
    protected Vector supply;
    protected ArrayList<Node> taken;

    Chooser method;

    public TransportationModel(Matrix costs,
                               Vector demand,
                               Vector supply,
                               Chooser approximationMethod
    ) throws ImproperConversionException {
        if (costs.getRows() != supply.size() || costs.getColumns() != demand.size()) {
            throw new ImproperConversionException("Wrong input data: demand consist from "
                    + demand.size() + " items; supply from "
                    + supply.size()
                    + " items; \nWhile costs matrix is (" + costs.getRows() + "x" + costs.getColumns() + ")");
        }
        this.costs = costs;
        this.demand = demand;
        this.supply = supply;
        taken = new ArrayList<>();
        method = approximationMethod;
    }

    public Matrix getFeasibleSolution(ArrayList<Node> taken) {
        Matrix solution = new Matrix(costs.getRows(), costs.getColumns());
        for (Node i : taken) {
            solution.set(i.row, i.col, i.provided);
        }
        return solution;
    }

    public Matrix solve() {
        while (taken.size() != Math.max(costs.getRows(), costs.getColumns())) {
            iteration();
        }
        return getFeasibleSolution(taken);
    }

    public void iteration() {
        Node item = method.choose(this);
        double provided = Math.min(supply.get(item.row), demand.get(item.col));
        demand.set(item.col, demand.get(item.col) - provided);
        supply.set(item.row, supply.get(item.row) - provided);
        item.setProvided(provided);
        item.setRowTaken(supply.get(item.row) <= demand.get(item.col));
        taken.add(item);
    }

    /**
     * Solution for 3rd homework
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read supply value vector
        System.out.println("Enter supply values (vector):");
        RowVector supply = RowVector.scan(scanner);

        // Read demand value vector
        System.out.println("Enter demand values (vector):");
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
                new NorthWestChooser(),
                new VogelChooser(),
                new RusselChooser()

        };

        // Solve using Interior Point method for alpha = 0.5 & alpha = 0.9
        for (Chooser method : methods) {
            try {
                TransportationModel solver = new TransportationModel(costs.clone(), demand.clone(), supply.clone(), method);
                Matrix optimalValue = solver.solve();
                System.out.println("The result of the " + method + " is\n" + optimalValue.toString());
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

