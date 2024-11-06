public class Node {
    int row, col;
    boolean rowTaken;
    public Node(int r, int c){
        row = r; col = c;
    }
    public Node(int r, int c, boolean who){
        row = r; col = c; rowTaken = who;
    }
}