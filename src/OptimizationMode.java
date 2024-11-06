// Enum representing the optimization modes for a problem
public enum OptimizationMode {
    MAX(-1, "Maximum"), MIN(1, "Minimum");

    public final int factor;
    public final String fullName;

    OptimizationMode(int factor, String name) {
        this.factor = factor;
        fullName = name;
    }
}
