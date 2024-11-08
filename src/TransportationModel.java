import Exceptions.ApplicationProblemException;
import Exceptions.DimensionsException;
import Exceptions.ImproperConversionException;
import Exceptions.TransportUnbalancedProblemException;

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
    ) throws ImproperConversionException, ApplicationProblemException, TransportUnbalancedProblemException {
        if (costs.getRows() != supply.size() || costs.getColumns() != demand.size()) {
            throw new ImproperConversionException("Wrong input data: demand consist from "
                    + demand.size() + " items; supply from "
                    + supply.size()
                    + " items; \nWhile costs matrix is (" + costs.getRows() + "x" + costs.getColumns() + ")");
        }
        try {
            // sum of all elements of a vector is a dot product with vector consist from only ones (\sum(a_i*1))
            if (demand.multiply(RowVector.one(demand.size(), 1d))
                    != supply.multiply(RowVector.one(supply.size(), 1d))) {
                throw new TransportUnbalancedProblemException("Supply and demand is not equal");
            }
        } catch (DimensionsException ignored) {
        }
        this.costs = costs;
        this.demand = demand;
        this.supply = supply;
        taken = new ArrayList<>();
        method = approximationMethod;
    }

    private boolean isAllDemandAndSupplySatisfied() {
        for (int i = 0; i < demand.size(); i++) {
            if (demand.get(i) > 0) {
                return false;
            }
        }

        for (int i = 0; i < supply.size(); i++) {
            if (supply.get(i) > 0) {
                return false;
            }
        }
        return true;
    }


    public Matrix solve() throws ApplicationProblemException {

        while (!isAllDemandAndSupplySatisfied()) {
            iteration();
        }
        Matrix solution = new Matrix(costs.getRows(), costs.getColumns());
        for (Node i : taken) {
            solution.set(i.row, i.col, i.provided);
        }
        return solution;
    }

    public void iteration() throws ApplicationProblemException {
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
     *
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

        // Read costs matrix
        System.out.println("Enter costs values (matrix):");
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

        // Solve using different approximations
        for (Chooser method : methods) {
            try {
                TransportationModel solver = new TransportationModel(costs.clone(), demand.clone(), supply.clone(), method);
                Matrix solution = solver.solve();
                System.out.println("The result of the " + method + " is\n" + solution);
            } catch (ApplicationProblemException e) {
                System.out.println("The " + method + " is not applicable!");
                return;
            } catch (TransportUnbalancedProblemException e) {
                System.out.println("The problem is not balanced!");
                return;
            } catch (ImproperConversionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

