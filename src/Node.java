public class Node {
    int row, col;
    boolean rowTaken;

    double provided;

    public Node(int r, int c, boolean who){
        row = r; col = c; rowTaken = who;
    }

    public Node(int r, int c) {
        row = r;
        col = c;
    }

    public void setProvided(double value) {
        provided = value;
    }

    public void setRowTaken(boolean value) {
        rowTaken = value;
    }
}