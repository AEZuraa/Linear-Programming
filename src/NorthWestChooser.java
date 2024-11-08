import Exceptions.ApplicationProblemException;

public class NorthWestChooser implements Chooser {
    @Override
    public Node choose(TransportationModel object) throws ApplicationProblemException {
        if (object.taken.isEmpty()) {
            return new Node(0, 0);
        }
        Node lastStep = object.taken.get(object.taken.size() - 1);
        Node nextNode = new Node(lastStep.row, lastStep.col);

        if (object.supply.get(lastStep.row) > object.demand.get(lastStep.col)) {
            nextNode.col++;
        } else {
            nextNode.row++;
        }
        return nextNode;
    }

    public String toString() {
        return "NorthWest approximation";
    }
}

