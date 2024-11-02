// Enum representing the optimization modes for a problem
public enum OptimizationMode {
    MAX(-1), MIN(1);

    public final int factor;
    OptimizationMode(int factor) {
        this.factor = factor;
    }
}
