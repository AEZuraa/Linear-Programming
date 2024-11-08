import java.util.ArrayList;
import java.util.List;

public class RusselChooser implements Chooser {
    @Override
    public Node choose(TransportationModel object) {

        int originalRows = object.costs.getRows();
        int originalCols = object.costs.getColumns();

        boolean[] rowsToExclude = new boolean[originalRows];
        boolean[] colsToExclude = new boolean[originalCols];

        for (Node node : object.taken) {
            if (node.rowTaken) {
                rowsToExclude[node.row] = true;
            } else {
                colsToExclude[node.col] = true;
            }
        }

        List<Integer> newRowIndices = new ArrayList<>();
        List<Integer> newColIndices = new ArrayList<>();

        for (int i = 0; i < originalRows; i++) {
            if (!rowsToExclude[i]) {
                newRowIndices.add(i);
            }
        }

        for (int j = 0; j < originalCols; j++) {
            if (!colsToExclude[j]) {
                newColIndices.add(j);
            }
        }

        Matrix newCosts = new Matrix(newRowIndices.size(), newColIndices.size());

        for (int i = 0; i < newRowIndices.size(); i++) {
            for (int j = 0; j < newColIndices.size(); j++) {
                newCosts.set(i, j, object.costs.get(newRowIndices.get(i), newColIndices.get(j)));
            }
        }

        double[] rowMaximum = new double[newCosts.getRows()];
        double[] columnMaximum = new double[newCosts.getColumns()];

        for (int i = 0; i < newCosts.getRows(); i++) {
            double max = Double.MIN_VALUE;
            for (int j = 0; j < newCosts.getColumns(); j++) {
                double currentCost = newCosts.get(i, j);
                if (currentCost > max) {
                    max = currentCost;
                }
            }
            rowMaximum[i] = max;
        }

        for (int j = 0; j < newCosts.getColumns(); j++) {
            double max = Double.MIN_VALUE;
            for (int i = 0; i < newCosts.getRows(); i++) {
                double currentCost = newCosts.get(i, j);
                if (currentCost > max) {
                    max = currentCost;
                }
            }
            columnMaximum[j] = max;
        }

        Matrix delta = new Matrix(newCosts.getRows(), newCosts.getColumns());

        for (int i = 0; i < newCosts.getRows(); i++) {
            for (int j = 0; j < newCosts.getColumns(); j++) {
                double currentCost = newCosts.get(i, j);
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


        if (minRow != -1 && minCol != -1) {
            minRow = newRowIndices.get(minRow);
            minCol = newColIndices.get(minCol);
        }
        return new Node(minRow, minCol);
    }

    @Override
    public String toString() {
        return "Russel's approximation";
    }
}