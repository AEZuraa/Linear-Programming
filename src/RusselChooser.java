public class RusselChooser implements Chooser {
    @Override
    public Node choose(TransportationModel object) {
        double[] rowMaximum = new double[object.costs.getRows()];
        double[] columnMaximum = new double[object.costs.getColumns()];
        for (int i = 0; i < object.costs.getRows(); i++) {
            double max = Double.MIN_VALUE;
            for (int j = 0; j < object.costs.getColumns(); j++) {
                double currentCost = object.costs.get(i, j);
                if (currentCost > max) {
                    max = currentCost;
                }
            }
            rowMaximum[i] = max;
        }

        for (int j = 0; j < object.costs.getColumns(); j++) {
            double max = Double.MIN_VALUE;
            for (int i = 0; i < object.costs.getRows(); i++) {
                double currentCost = object.costs.get(i, j);
                if (currentCost > max) {
                    max = currentCost;
                }
            }
            columnMaximum[j] = max;
        }

        Matrix delta = new Matrix(object.costs.getRows(), object.costs.getColumns());

        for (int i = 0; i < object.costs.getRows(); i++) {
            for (int j = 0; j < object.costs.getColumns(); j++) {
                double currentCost = object.costs.get(i, j);
                double result = currentCost - rowMaximum[i] - columnMaximum[j];
                delta.set(i, j, result);
            }
        }

        double minValue = Double.MAX_VALUE;
        int minRow = -1;
        int minCol = -1;

        for (int i = 0; i < delta.getRows(); i++) {
            for (int j = 0; j < delta.getColumns(); j++) {
                double value = delta.get(i, j);
                if (value < minValue) {
                    minValue = value;
                    minRow = i;
                    minCol = j;
                }
            }
        }

        return new Node(minRow, minCol);
    }

    @Override
    public String toString() {
        return "Russel Approximation";
    }
}
