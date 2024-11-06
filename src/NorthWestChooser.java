public class NorthWestChooser implements Chooser {
    private int currentRow = 0;
    private int currentCol = 0;

    @Override
    public Node choose(TransportationModel object) {
        Node chosenNode = new Node(currentRow, currentCol);
        object.taken.add(chosenNode);

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
}

