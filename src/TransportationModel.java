import Exceptions.ImproperConversionException;

import java.util.ArrayList;

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

    public double solve() {
        while (taken.size() == Math.max(costs.getRows(), costs.getColumns())) {
            iteration();
        }
        double res = 0;
        for (Node item : taken) {
            res += costs.get(item.row, item.col) * item.provided;
        }
        return res;
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
}

