import Exceptions.ApplicationProblemException;
import Exceptions.DimensionsException;
import Exceptions.SingularityException;

public class InteriorTopologicalPoint {
    Vector objectiveFunction;
    Matrix constrains;
    Vector currentPoint;
    double alpha;
    DoublePreciseComparator cmp;

    public InteriorTopologicalPoint(
            Vector objectiveFunction,
            Matrix constrains,
            Vector rightHandSide,
            Vector initialPoint,
            double alpha,
            double accuracy
    ) throws DimensionsException, ApplicationProblemException {
        cmp = new DoublePreciseComparator(accuracy);
        // TODO: name for temp && check if this work correctly, fix the check
        Vector temp;
        try {
            temp = constrains.multiply(initialPoint);
        } catch (DimensionsException e) {
            throw new DimensionsException("Right hand side vector must have size of matrix columns amount");
        }
        temp.mutateBy(rightHandSide, (a, b) -> a - b);
        if (!temp.all((a) -> cmp.compare(a, 0d) >= 0)) {
            throw new ApplicationProblemException("Interior point must be inside the topological region");
        }
        this.objectiveFunction = objectiveFunction;
        this.constrains = constrains;
        this.alpha = alpha;
    }

    public Vector solve() throws DimensionsException, SingularityException {
        while (iteration()) {
        }
        return currentPoint;
    }

    // TODO: doc, naming, error catching (think, which exceptions can be thrown, which not, which could be handled while initialization)
    protected boolean iteration() throws DimensionsException, SingularityException {
        Matrix D = Matrix.diagonal(currentPoint);
        Matrix ATilda = constrains.multiply(D);
        Vector cTilda = D.multiply(objectiveFunction);
        Matrix P = Matrix.Identity(ATilda.getRows()).subtract(ATilda.getPseudoInverse(cmp.accuracy));
        Vector cp = P.multiply(cTilda);
        double factor = alpha / Math.abs(cp.get(cp.theMost((a, b) -> a < b)));
        Vector xTilda = RowVector.one(currentPoint.size(), 1);
        xTilda.mutateBy(cp, (one, value) -> one + factor * value);
        Vector xStar = D.multiply(xTilda);
        if (cmp.compare(xStar.getMutated(currentPoint, (a, b) -> a - b).cardinality(), 0d) <= 0) {
            currentPoint = xStar;
            return true;
        }
        currentPoint = xStar;
        return false;
    }
}
