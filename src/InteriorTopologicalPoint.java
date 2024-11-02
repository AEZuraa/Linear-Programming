import Exceptions.ApplicationProblemException;
import Exceptions.DimensionsException;
import Exceptions.SingularityException;

public class InteriorTopologicalPoint {
    Vector objectiveFunction;
    Matrix constrains;
    Vector currentPoint;
    double alpha;
    DoublePreciseComparator cmp;
    OptimizationMode mode;

    public InteriorTopologicalPoint(
            Vector objectiveFunction,
            Matrix constraints,
            Vector rightHandSide,
            Vector initialPoint,
            double alpha,
            double accuracy,
            OptimizationMode mode
    ) throws DimensionsException, ApplicationProblemException {
        cmp = new DoublePreciseComparator(accuracy);
        Vector initialLHS;
        try {
            initialLHS = constraints.multiply(initialPoint);
        } catch (DimensionsException e) {
            throw new DimensionsException("Right hand side vector must have size of matrix columns amount");
        }
        initialLHS.mutateBy(rightHandSide, (a, b) -> a - b);
        initialLHS.multiply(-1);
        if (!initialLHS.all((a) -> cmp.compare(a, 0d) <= 0)) {
            throw new ApplicationProblemException("Interior point must be inside the topological region");
        }
        this.objectiveFunction = objectiveFunction.multiply(mode.factor);
        this.constrains = constraints.combineRight(Matrix.Identity(constraints.rows));
        this.alpha = alpha;
        this.currentPoint = initialPoint.extendWith(initialLHS);
        this.mode = mode;
    }

    public Vector solve() throws DimensionsException, SingularityException {
        while (iteration()) {
        }
        return currentPoint.multiply(mode.factor*(-1));
    }

    // TODO: doc
    protected boolean iteration() throws DimensionsException, SingularityException {
        Matrix D = Matrix.diagonal(currentPoint);
        Matrix ATilda;
        try {
            ATilda = constrains.multiply(D);
        } catch (DimensionsException e) {
            throw new DimensionsException("The amount of columns in constraints matrix must be equal to the amount of rows in diagonal solution matrix");
        }
        Vector cTilda;
        try {
            cTilda = D.multiply(objectiveFunction);
        } catch (DimensionsException e) {
            throw new DimensionsException("The amount of columns in diagonal solution matrix must be equal to the amount of coefficients in objective function vector");
        }
        Matrix hui = ATilda.getPseudoInverse(cmp.accuracy);
        Matrix P = Matrix.Identity(ATilda.getColumns()).subtract(hui);
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
