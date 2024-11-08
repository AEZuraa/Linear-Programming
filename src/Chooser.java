import Exceptions.ApplicationProblemException;

public interface Chooser {
    Node choose(TransportationModel object) throws ApplicationProblemException;
}
