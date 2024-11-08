import Exceptions.ApplicationProblemException;

public class RusselChooser implements Chooser {
    @Override
    public Node choose(TransportationModel object) throws ApplicationProblemException {
        double[] rowMaximum = new double[object.costs.getRows()];
        double[] colMaximum = new double[object.costs.getColumns()];
        for (int i = 0; i < object.costs.getRows(); i++) {
            RowVector curRow = object.costs.get(i);
            rowMaximum[i] = curRow.get(curRow.theMost((a, b) -> a > b));
        }

        for (int i = 0; i < object.costs.getColumns(); i++) {
            ColumnVector curCol = new ColumnVector(object.costs, i);
            colMaximum[i] = curCol.theMost((a, b) -> a > b);
        }

        Matrix delta = new Matrix(object.costs.getRows(), object.costs.getColumns());

        for (int i = 0; i < object.costs.getRows(); i++) {
            for (int j = 0; j < object.costs.getColumns(); j++) {
                double currentCost = object.costs.get(i, j);
                double result = currentCost - rowMaximum[i] - colMaximum[j];
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
