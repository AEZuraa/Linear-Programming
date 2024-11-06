public class NorthWestChooser implements Chooser {
    private int currentRow = 0;
    private int currentCol = 0;

    @Override
    public Node choose(TransportationModel object) {
        Node chosenNode = new Node(currentRow, currentCol);

        if (object.supply.get(currentRow) > object.demand.get(currentCol)) {
            currentRow++;
        } else if (object.supply.get(currentRow) < object.demand.get(currentCol)) {
            currentCol++;
        } else {
            currentRow++;
            currentCol++;
        }
        return chosenNode;
    }

    public String toString() {
        return "NorthWest approximation";
    }
}

