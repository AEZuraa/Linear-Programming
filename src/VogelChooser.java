import Exceptions.ApplicationProblemException;

import java.util.List;
import java.util.ArrayList;

public class VogelChooser implements Chooser {
    @Override
    public String toString() {
        return "Vogel's approximation";
    }

    @Override
    public Node choose(TransportationModel object) throws ApplicationProblemException {
        List<Double> smallestElementsDifferencesRows = new ArrayList<>();
        List<Double> smallestElementsDifferencesCols = new ArrayList<>();
        for (int i = 0; i < Math.max(object.costs.getRows(), object.costs.getColumns()); i++) {
            double minR1 = Double.MAX_VALUE;
            double minR2 = Double.MAX_VALUE;

            double minC1 = Double.MAX_VALUE;
            double minC2 = Double.MAX_VALUE;

            for (int j = 0; j < Math.max(object.costs.getRows(), object.costs.getColumns()); j++) {
                if (i < object.costs.getRows() && j < object.costs.getColumns()) {
                    double valueR = object.costs.get(i, j);
                    if (valueR < minR1) {
                        minR2 = minR1;
                        minR1 = valueR;
                    } else if (valueR < minR2) {
                        minR2 = valueR;
                    }
                }
                if (i < object.costs.getColumns() && j < object.costs.getRows()) {
                    double valueC = object.costs.get(j, i);
                    if (valueC < minC1) {
                        minC2 = minC1;
                        minC1 = valueC;
                    } else if (valueC < minC2) {
                        minC2 = valueC;
                    }
                }
            }
            smallestElementsDifferencesRows.add(Math.abs(minR1-minR2));
            smallestElementsDifferencesCols.add(Math.abs(minC1-minC2));
        }
        double max1 = smallestElementsDifferencesRows.getFirst();
        int indmax1 = 0;

        for (int i = 1; i < smallestElementsDifferencesRows.size(); i++) {
            if (smallestElementsDifferencesRows.get(i) > max1) {
                max1 = smallestElementsDifferencesRows.get(i);
                indmax1 = i;
            }
        }

        double max2 = smallestElementsDifferencesCols.getFirst();
        int indmax2 = 0;

        for (int i = 1; i < smallestElementsDifferencesCols.size(); i++) {
            if (smallestElementsDifferencesCols.get(i) > max2) {
                max2 = smallestElementsDifferencesCols.get(i);
                indmax2 = i;
            }
        }
        double min = Double.MAX_VALUE;
        int indmin = -1;

        if (max1 > max2) {
            for (int j = 0; j < object.costs.getColumns(); j++) {
                double value = object.costs.get(indmax1, j);

                if (value < min) {
                    min = value;
                    indmin = j;
                }
            }

            return new Node(indmax1, indmin);
        }

        for (int i = 0; i < object.costs.getRows(); i++) {
            double value = object.costs.get(i, indmax2);

            if (value < min) {
                min = value;
                indmin = i;
            }
        }

        return new Node(indmin, indmax2);
    }
}
