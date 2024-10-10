import java.util.Comparator;

/** Comparator, used when doubles should be compared with respect to some accuracy ((a==b) == (abs(a-b) < e)) */
public class DoublePreciseComparator implements Comparator<Double> {
    double accuracy;
    /**
     * Comparator, used when doubles should be compared with respect to some accuracy ((a==b) == (abs(a-b) < e))
     * @param accuracy e
     */
    public DoublePreciseComparator(double accuracy){
        this.accuracy = accuracy;
    }


    @Override
    public int compare(Double o1, Double o2) {
        double dif = o1 - o2;
        if (dif > accuracy) {
            return 1;
        } else if (dif < -accuracy) {
            return -1;
        } else {
            return 0;
        }
    }
}
