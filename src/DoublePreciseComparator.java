import java.util.Comparator;

public class DoublePreciseComparator implements Comparator<Double> {
    double accuracy;
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
